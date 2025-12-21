package com.chen;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.constant.UserConstant;
import com.chen.mapper.*;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.*;
import com.chen.pojo.properties.JwtProperties;
import com.chen.pojo.properties.TencentMapProperties;
import com.chen.service.UserService;
import com.chen.task.Task2Service;
import com.chen.util.JwtUtil;
import com.chen.util.TencentMapUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static com.chen.constant.TencentConstant.*;
import static com.chen.constant.UserConstant.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ComponentScan("com.chen")
class SpringAiDemoApplicationTests {


    @Autowired
    private AIMessageMapper aiMessageMapper;

    @Test
    void contextLoads() {
        List<AIMessage> messages = aiMessageMapper.getMessages("1");
        System.out.println(messages);
    }

    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 测试用户的jwt生成解析
     */
    @Test
    void testJwtUtil() {
        String signKey = jwtProperties.getUserSignKey();
        long time = jwtProperties.getUserTtl();
        UserDTO build = UserDTO.builder()
                .nickName("chen")
                .email("chen@gmail.com")
                .password("123456")
                .build();

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(UserConstant.NICKNAME, build.getNickName());
        claims.put(UserConstant.EMAIL, build.getEmail());

        String token = JwtUtil.generateUserToken(signKey, time, claims);
        System.out.println(token);
        Jws<Claims> claimsJws = JwtUtil.parseUserToken(signKey, token);
        String nickName = claimsJws.getPayload().get("nickName", String.class);
        System.out.println(nickName);


       /* Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(UserConstant.NICKNAME, build.getNickName());
        claims.put(UserConstant.EMAIL, build.getEmail());

        SecretKey key = Jwts.SIG.HS256.key().build();
        String token = Jwts.builder()
                .signWith(key)
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .compact();
        System.out.println(token);

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        System.out.println(claimsJws.getPayload().get("nickName", String.class));*/

    }

    @Test
    void testSubStr() {
        String str = "org.springframework.dao.DuplicateKeyException: \n" +
                "### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '1@qq.com' for key 'tb_ai_user.email'\n" +
                "### The error may exist in com/chen/mapper/UserMapper.java (best guess)\n" +
                "### The error may involve com.chen.mapper.UserMapper.insert-Inline\n" +
                "### The error occurred while setting parameters\n" +
                "### SQL: insert into tb_ai_user(nick_name, gender, birthday, password, email, points, enable, is_del, register_time) value (?,?,?,?,?,?,?,?,?)\n" +
                "### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '1@qq.com' for key 'tb_ai_user.email'\n" +
                "; Duplicate entry '1@qq.com' for key 'tb_ai_user.email";
        int index = str.indexOf(" for key ");
        String duplicateEntry = str.substring(index - 10, index);
        System.out.println(duplicateEntry);
    }


    @Test
    void testMatch() {
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regex);

        pattern.matcher("chen@gmail.com").matches();
        System.out.println(pattern.matcher("chen@gmail.com").matches());

        System.out.println(pattern.matcher("1111").matches());
    }

    @Test
    void testJwtProperties() {
        System.out.println(jwtProperties);
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testRedis() {
     /*  LocalDateTime now = LocalDateTime.now();
      String key_prefix = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+":";
      Random r=new Random();
      String key_suffix="3406339653"+r.nextInt(10)+"@qq.com";
      String key = key_prefix+key_suffix;
     stringRedisTemplate.opsForValue().set(key,"1111");*/

//      Boolean delete = stringRedisTemplate.delete("2025-12-11:*");

   /*   DefaultRedisScript<Long> script = new DefaultRedisScript<>();

      script.setLocation(new ClassPathResource("lua/DeleteLoginCode.lua"));
      script.setResultType(Long.class);

      Long execute = stringRedisTemplate.execute(script, Collections.emptyList(), "2025-12-11:*");
      System.out.println(execute);*/

    }

    @Autowired
    private Task2Service task2Service;
    @Autowired
    private UserService userService;

    @Test
    void testTask() {

        /// ////用于验证....
      /*for (int i = 0; i < 10; i++) {
          String code = RandomUtil.randomString(4);

          //生成一天的相同前缀，统一删除
          String pattern = "yyyy-MM-dd";
          String time_prefix = LocalDateTime.now().plusDays(-1).format(DateTimeFormatter.ofPattern(pattern)) + ":";

          String key = USER_LOGIN + time_prefix + "340633"+i+"9653@qq.com";
          // 加入防暴力破解的次数限制
          stringRedisTemplate.opsForValue().set(key, code);
          System.out.println(code);
      }*/
        task2Service.delUserLoginCode();
    }

    @Resource
    private AISessionMapper sessionMapper;

    @Test
    void testMapper() {

      /*AISessionDTO sessionDTO = AISessionDTO
              .builder()
              .userId(null)
              .isDel(1).build();
      List<AISession> aiSessions = sessionMapper.queryByUserId(sessionDTO);

      aiSessions.stream().filter(session->
              !"sess_004_20240101".equals(session.getSessionId())
              ).toList().forEach(System.out::println);*/


     /* List<AIMessage> messages = aiMessageMapper.getMessages("sess_001_20240101");
      if (messages == null || messages.isEmpty()) {
         return Collections.emptyList();
      }

      List<AIMessageVO> list = messages.stream().filter(
                      item -> !MessageType.SYSTEM.equals(item.getType())
              ).sorted((v1, v2) -> v1.getCreatedTime().isBefore(v2.getCreatedTime()) ? -1 : 1)
              .map(item -> BeanUtil.copyProperties(item, AIMessageVO.class))
              .toList();
      System.out.println(list);
      */

        AISessionDTO sessionDTO = AISessionDTO.builder()
                .userId(1)
                .isDel(NODEL)
                .build();

        List<AISession> list = sessionMapper.queryByUserId(sessionDTO);
        System.out.println(list);
    }

    @Autowired
    private TencentMapProperties tencentMapProperties;

    @SneakyThrows
    @Test
    void testTencentMap() throws JSONException, UnsupportedEncodingException {


        Map<String, String> map = new TreeMap<>();
        map.put("key", tencentMapProperties.getApiKey());
        map.put("location", "39.9042,116.4074");


        String ad = TencentMapUtil.generateAddress(map, tencentMapProperties.getSecretKey());
        System.out.println(ad);


    }

    @Resource
    private SpringTemplateEngine springTemplateEngine;


    @Resource
    ModelMapper modelMapper;
    @Resource
    CompanyMapper companyMapper;


    @Autowired
    @Qualifier("iTemplateResolver2")
    private ITemplateResolver iTemplateResolver;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void testTemplate() {

        //1.你的名字是${modelName}，你的性格是${model-name}，语言风格要符合这一点。
        //2.你的公司名字是${model-company-name}，其位置在${model-company-address}，
        // 你招聘员工的薪资一个月在${model-company-lowSalary}到${model-company-highSalary}之间，
        // 你招聘的工作标签为${model-company-jobTag}，员工福利有${model-company-benefits}。


        templateEngine.setTemplateResolver(iTemplateResolver);

        List<Model> models = modelMapper.queryAllModels();
        for (Model model : models) {
            String companyId = model.getCompanyId();
            Company company = companyMapper.selectCompanyId(companyId);
            Context ctx = new Context();
            Map<String, Object> data = new HashMap<>();
            data.put("modelName", model.getName());
            if (model.getTemperature() >= 0.0 && model.getTemperature() <= 0.7) {
                data.put("modelPersonality", "强硬型");
            } else if (model.getTemperature() > 0.7 && model.getTemperature() <= 1.5) {
                data.put("modelPersonality", "幽默型");
            } else if (model.getTemperature() > 1.5 && model.getTemperature() <= 2.0) {
                data.put("modelPersonality", "卑微型");
            }
            data.put("modelCompanyName", company.getName());

            Map<String, String> map = new TreeMap<>();
            map.put(TENCENT_LOCATION, company.getAddress());
            String apiKey = tencentMapProperties.getApiKey();
            map.put(TENCENT_API_KEY, apiKey);
            String address = TencentMapUtil.generateAddress(map, tencentMapProperties.getSecretKey());
            data.put("modelCompanyAddress", address);
            data.put("modelCompanyLowSalary", company.getLowSalary());
            data.put("modelCompanyHighSalary", company.getHighSalary());
            data.put("modelCompanyJobTag", company.getJobTag());
            data.put("modelCompanyBenefits", company.getEmployerBenefit());
            ctx.setVariables(data);
            String process = templateEngine.process("ModelPrompt.txt", ctx);
            System.out.println(process);

            model.setDescription(process);
            modelMapper.update(model);

     /*   Context ctx = new Context();
        ctx.setVariable("companyName", "邦邦科技");
        ctx.setVariable("jobTitle", "孔孟");
        ctx.setVariable("salary", "30K");
        String process = templateEngine.process("generateOffer.html", ctx);
        System.out.println(process);*/
        }
    }

    @Test
    void testUUID() {
        String string = UUID.randomUUID().toString(true);
        System.out.println(string);

    }

    @Resource
    private UserMapper userMapper;

    @Test
    void testUser() {
        for (int i = 0; i < 1000; i++) {
            int randomInt = RandomUtil.randomInt(0, 2);
            String nickName = RandomUtil.randomString(10);
            String password = DigestUtils.md5DigestAsHex(RandomUtil.randomString(8).getBytes());
            DateTime startDate = DateUtil.parse("2000-01-01");  // 月份从0开始
            DateTime randomDate = RandomUtil.randomDate(startDate, DateField.DAY_OF_YEAR, 1, 360);
            String[] strings = {
                    "@qq.com", "@163.com", "@gmail.com", "@outlook.com", "@edu.cn", "@126.com",
                    "@yahoo.com.cn", "@sina.com", "@sohu.com"
            };
            String email = RandomUtil.randomString(10) + strings[RandomUtil.randomInt(0, strings.length)];

            String[] images = {"https://chat-springai-store.oss-cn-beijing.aliyuncs.com/2025/12/demo1.jpg"
                    , "https://chat-springai-store.oss-cn-beijing.aliyuncs.com/2025/12/demo22.jpg",
                    "https://chat-springai-store.oss-cn-beijing.aliyuncs.com/2025/12/demo2223.jpg",
                    "https://chat-springai-store.oss-cn-beijing.aliyuncs.com/2025/12/gfe233.jpg",
            };

            String img = images[RandomUtil.randomInt(0, images.length)];

            User user = User.builder()
                    .points(1000)
                    .password(password)
                    .birthday(randomDate)
                    .gender(randomInt)
                    .nickName(nickName)
                    .enable(randomInt)
                    .isDel(randomInt)
                    .email(email)
                    .image(img)
                    .registerTime(LocalDateTime.now().minusDays(RandomUtil.randomInt(200)))
                    .build();

            userMapper.insert(user);
        }
    }

    @Test
    void testUserReport() {

        List<Map<String, Object>> maps = userMapper.queryTemplateReport();


        System.out.println(maps);


    }

    @Test
    void testTencentMaps() {
        Map<String,String> map=new TreeMap<>();
        map.put(TENCENT_ADDRESS,"北京市东城区正义路");
        String apiKey = tencentMapProperties.getApiKey();
        map.put(TENCENT_API_KEY,apiKey);
        String location = TencentMapUtil.parseAddress(map, tencentMapProperties.getSecretKey());
        System.out.println(location);
    }

    @Test
    void testCompanyTag(){



        List<String> tag= List.of("数据科学家","算法","数据分析");

        String join = StrUtil.join(" ",tag);
        System.out.println(join);

    }


}
