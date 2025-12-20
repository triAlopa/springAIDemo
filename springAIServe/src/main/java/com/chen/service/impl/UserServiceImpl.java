package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.exception.AccountBusinessException;
import com.chen.exception.AccountRegisterException;
import com.chen.exception.LoginException;
import com.chen.mapper.AISessionMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.PageResult;
import com.chen.pojo.Result;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.dto.QueryUserDTO;
import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.AISession;
import com.chen.pojo.entity.User;
import com.chen.pojo.properties.JwtProperties;
import com.chen.pojo.vo.UserVO;
import com.chen.pojo.vo.report.EmailReportVO;
import com.chen.service.UserService;
import com.chen.util.AliyunOSSOperator;
import com.chen.util.CurrentUserHolder;
import com.chen.util.JwtUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.chen.constant.RedisConstant.*;
import static com.chen.constant.ResultConstant.*;
import static com.chen.constant.ResultConstant.HTTPSTATUS.BAD_REQUEST;
import static com.chen.constant.ResultConstant.HTTPSTATUS.NOT_FOUND;
import static com.chen.constant.UserConstant.*;
import static com.chen.constant.UserConstant.RegisterOrLOGIN.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private TemplateEngine templateEngine;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtProperties jwtProperties;

    @Resource
    private AliyunOSSOperator aliyunOSSOperator;

    @Resource
    private AISessionMapper sessionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertSingleUser(UserDTO userDTO, HttpServletResponse response) {

        if (userDTO == null || userDTO.getEmail() == null) {
            //zui弱智的校验，但是要加
            throw new AccountRegisterException(INPUT_PARAMS_ERR, BAD_REQUEST);
        }

        //邮箱验证码验证
        String key = USER_REGISTER + userDTO.getEmail();
        String originCode = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(originCode)) {
            throw new AccountRegisterException(UNCODE, BAD_REQUEST);
        }
        //  邮箱验证码的验证是否一致
        if (!originCode.equals(userDTO.getEmailCode())) {
            throw new AccountRegisterException(INPUT_CODE_ERR, BAD_REQUEST);
        }

        User user = handleInsertByLogical(userDTO);
        //  token 返回

        String token = generateUserToken(user);

        return token;
    }

    private User handleInsertByLogical(UserDTO userDTO) {
        //校验用户是否注册
        User checkUser = userMapper.selectByEmail(userDTO.getEmail());
        if (checkUser != null) {
            //表示已经注册过了
            //已经在全局异常处理了,鸡蛋几个日志
            log.error(UNREGISTER);
        }
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(password)) {
            //密码为空
            throw new AccountRegisterException(PASSWORD_INVALID, BAD_REQUEST);
        }
        //密文存储
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        userDTO.setPassword(password);

        //转为实体类 插入数据库
        User user = User.
                builder()
                .registerTime(LocalDateTime.now())
                .enable(ENABLE)
                .isDel(NODEL)
                .points(DEFAULT_POINTS)
                .build();
        BeanUtil.copyProperties(userDTO, user);

        userMapper.insert(user);
        return user;
    }

    @Override
    public void sendEmailCode(String nickName, String email) {
        //复杂的消息处理
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            //发送者
            mimeMessageHelper.setFrom(from);
            mimeMessage.setSubject("boss求聘平台验证码");
            //为邮箱html变量赋值
            //1.生成随机验证码
            String code = RandomUtil.randomString(6);
            //2.保留时间 超时失效
            //反序列化 email就是去除双引号....
            String key = USER_REGISTER + email;
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, code, USER_REGISTER_TTL, TimeUnit.MILLISECONDS);
            if (BooleanUtil.isFalse(flag)) {
                //当前用户验证码未过期
                throw new AccountRegisterException(REPEAT_REQUEST_CODE, BAD_REQUEST);
            }
            //设置html处理变量
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("nickName", nickName);
            //处理为字符串发送
            String template = templateEngine.process("emailTemplate", context);
            //确认发送文本
            mimeMessageHelper.setText(template, true);
            //收件人
            mimeMessageHelper.setTo(email);
            //发送
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("send email code error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateLoginCode(String email) {
        String code = RandomUtil.randomString(4);

        //生成一天的相同前缀，统一删除
        String pattern = "yyyy-MM-dd";
        String time_prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + ":";

        String key = USER_LOGIN + time_prefix + email;
        //TODO 加入防暴力破解的次数限制
        stringRedisTemplate.opsForValue().set(key, code);

        return code;
    }

    @Override
    public String querySingleUser(UserDTO user) {
        if (user == null || user.getEmail() == null) {
            throw new LoginException(INPUT_PARAMS_ERR);
        }

        String email = user.getEmail();
        User selected = userMapper.selectByEmail(email);
        //邮箱就不存在数据库
        if (selected == null) {
            throw new LoginException(USER_IEXIST_EMAIL, NOT_FOUND);
        }
        //用户输入密码加密校验
        String inputPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

        //密码不正确
        if (!selected.getPassword().equals(inputPassword)) {
            throw new LoginException(USER_INPUT_PASS_ERR, BAD_REQUEST);
        }

        User usr = new User();
        BeanUtil.copyProperties(selected, usr);
        // 生成token 返回
        String token = generateUserToken(usr);

        return token;
    }

    @Override
    public UserVO queryUserInfo() {
        //获取该线程的userDto
        UserDTO userDTO = CurrentUserHolder.getCurrentUser();
        Integer userId = userDTO.getId();

        User user = userMapper.selectById(userId);
        UserVO userVo = new UserVO();
        BeanUtil.copyProperties(user, userVo);

        return userVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String modifyUserPassword(Integer userId, UserChangePassDTO changePassDTO) {
        User user = userMapper.selectById(userId);

        //旧的pass，那新的呢
        String oldPass = user.getPassword();
        String userInputOldPass = changePassDTO.getOriginPassword();
        //原来密码为空
        if (StrUtil.isBlank(userInputOldPass)) {
            throw new AccountBusinessException(PASSWORD_INVALID, BAD_REQUEST);
        }
        //将用户传来的旧密码加密匹配
        String digestOldUsr = DigestUtils.md5DigestAsHex(userInputOldPass.getBytes());
        //原密码不匹配
        if (!oldPass.equals(digestOldUsr)) {
            throw new AccountBusinessException(OLD_PASSWORD_ERR, BAD_REQUEST);
        }

        changePassDTO.setId(userId);
        //更新originPass
        changePassDTO.setOriginPassword(digestOldUsr);
        //更新changePass
        changePassDTO.setChangedPassword(DigestUtils.md5DigestAsHex(
                changePassDTO.getChangedPassword().getBytes()
        ));
        userMapper.updateSingleUser(changePassDTO);

        //更新token
        return generateUserToken(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadUserImage(MultipartFile file) {

        Integer userId = CurrentUserHolder.getCurrentUser().getId();

        try {
            String suffix = file.getOriginalFilename().substring(file
                    .getOriginalFilename()
                    .lastIndexOf("."));

            String fileName = UUID.randomUUID().toString(true) + suffix;

            // 上传文件
            String url = aliyunOSSOperator.upload(file.getBytes(), fileName);


            User user = User.builder()
                    .id(userId).image(url).build();

            userMapper.updateUser(user);

//            userMapper.updateUserImage(url,userId);

            return url;
        } catch (Exception e) {
            log.error("{}用户上传错误,message:{}", userId, e.getMessage());
            throw new RuntimeException("上传图片失败");
        }
    }

    @Override
    public PageResult<List<UserVO>> queryAllUser(QueryUserDTO queryUserDTO) {

        Integer pageNum = queryUserDTO.getPageNum();
        Integer pageSize = queryUserDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<User> userList = userMapper.queryAllUser(queryUserDTO);

        PageInfo<User> pageInfo = new PageInfo<>(userList);

        List<UserVO> list = pageInfo.getList().stream().map(
                user -> BeanUtil.copyProperties(user, UserVO.class)
        ).toList();

        return new PageResult<>(list, pageInfo.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSingleUser(UserDTO userDTO) {

        //添加操作
        if (userDTO.getId() == null) {
            handleInsertByLogical(userDTO);
            return;
        }
        //修改操作
        User user = userMapper.selectById(userDTO.getId());
        if (userDTO.getPassword() != null) {
            String password = DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes());
            userDTO.setPassword(password);
        }

        BeanUtil.copyProperties(userDTO, user);
        userMapper.updateUser(user);

    }

    @Override
    public UserVO selectById(Integer userId) {
        User user = userMapper.selectById(userId);
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByLogical(Integer id) {

        User user = User.builder()
                .isDel(ISDEL)
                .id(id)
                .cancelTime(LocalDate.now())
                .build();

        handleLogicalDelUserSession(id);

        userMapper.updateUser(user);
    }

    private void handleLogicalDelUserSession(Integer id) {
        AISessionDTO sessionDTO = AISessionDTO.builder()
                .userId(id)
                .isDel(NODEL).build();

        List<AISession> aiSessions = sessionMapper.queryByUserId(sessionDTO);

        if (aiSessions != null && !aiSessions.isEmpty()) {
            aiSessions.forEach(session -> {
                session.setIsDel(ISDEL);
            });

            sessionMapper.batchUpdateSession(aiSessions);
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsersByLogical(List<Integer> ids) {

        userMapper.updateByLogicalDelIds(ids);

        ids.forEach(this::handleLogicalDelUserSession);

    }

    @Override
    public List queryUserRegisterInfo(LocalDateTime start) {

        return userMapper.getUserReport(start);
    }

    @Override
    public List<EmailReportVO> getEmailStats() {
        return userMapper.getEmailReport();
    }

    @Override
    public void getUserReportExcel(HttpServletResponse response) {

        List<Map<String, Object>> maps = userMapper.queryTemplateReport();
//[{enable=1, is_del=1, count(*)=523}, {enable=0, is_del=0, count(*)=475}, {enable=1, is_del=0, count(*)=5}]
        Integer userRegCount = 0;
        Integer userDelCount = 0;
        Integer userIllegalCount=0;
        Integer number=0;

        for (Map<String, Object> map : maps) {
            int enable =(( Integer) map.get("enable") );
            int isDel =(( Integer) map.get("is_del") );
            int count =(( Long) map.get("count") ).intValue();
            number+=count;
            if(isDel==0) userDelCount+=count;
            if(enable==0) userIllegalCount+=count;
        }
        userRegCount=number-userDelCount;


        //导出excel 获得类加载器的输入流,获取资源路径 资源根路径
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/xlsx/用户模板.xlsx");
        //创建excel对象
        try {
            XSSFWorkbook excel = new XSSFWorkbook(is);
            //获取模板的标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //写入日期
            sheet.getRow(1).getCell(1).setCellValue("报表日期:" + LocalDate.now() );
            //获取行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(userRegCount);
            row.getCell(4).setCellValue(userDelCount);
            row.getCell(6).setCellValue((userDelCount.doubleValue()/number));
            //获取行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(userIllegalCount);

            List<User> userList = userMapper.queryAllUser(null);

            //批量加入三十天前每一天的
            for (int i = 0; i < userList.size(); i++) {
                //获取行
                User user = userList.get(i);
                row = sheet.getRow(7 + i);

                if(row==null){
                    sheet.createRow(7 + i);
                }

                row.getCell(1).setCellValue(user.getId());//id
                row.getCell(2).setCellValue(user.getNickName());//用户名
                row.getCell(3).setCellValue(user.getEmail());//邮箱
                row.getCell(4).setCellValue(user.getPoints());//积分
                row.getCell(5).setCellValue(user.getEnable()==1?"否":"是");//是否违规
                row.getCell(6).setCellValue(user.getRegisterTime());//注册时间
            }

            //获取网页端 输出流 写出数据
            ServletOutputStream os = response.getOutputStream();
            //关流
            excel.write(os);
            os.close();
            excel.close();
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 生成token
     *
     * @param user
     * @return
     */
    private String generateUserToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(USER_ID, user.getId());
        claims.put(EMAIL, user.getEmail());

        String Key = jwtProperties.getUserSignKey();
        long userTTL = jwtProperties.getUserTtl();

        String token = JwtUtil.generateUserToken(Key, userTTL, claims);
        log.info("已经为id：{}的用户生成对应token", user.getId());
        return token;
    }
}
