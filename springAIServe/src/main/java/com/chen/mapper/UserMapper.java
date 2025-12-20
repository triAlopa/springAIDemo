package com.chen.mapper;

import com.chen.pojo.dto.QueryUserDTO;
import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.entity.User;
import com.chen.pojo.vo.report.EmailReportVO;
import com.chen.pojo.vo.report.UserReportVO;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {


    @Insert("insert into tb_ai_user(nick_name, gender, birthday, password,image, " +
            "email, points, enable, is_del, register_time)" +
            " value (#{nickName},#{gender},#{birthday},#{password},#{image},#{email}," +
            "#{points},#{enable},#{isDel},#{registerTime})")
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
     void insert(User user);


    @Select("select * from tb_ai_user where email=#{email}")
    User selectByEmail(String email);

    User selectById(Integer userId);

    void updateSingleUser(UserChangePassDTO changePassDTO);

    /*@Update("update tb_ai_user set image=#{image} where id=#{userId}")
    void updateUserImage(String image,Integer userId);*/

    List<User> queryAllUser(QueryUserDTO queryUserDTO);

    void updateUser(User user);


    void updateByLogicalDelIds(List<Integer> ids);

   /* @Select("select (case when is_del = 1 then '注册'  else '注销' end ) as type, count(*) as count from tb_ai_user " +
            "where register_time between #{start} and #{end} group by date(register_time) ,is_del ORDER BY date (register_time) asc")
    List<Map<String, Integer> >  selectRegister4report(LocalDateTime start, LocalDateTime end);*/

    List<UserReportVO> getUserReport(LocalDateTime start);

    List<EmailReportVO> getEmailReport();

    @Select("select  count(*) as count ,is_del,enable  from tb_ai_user group by is_del ,enable")
    List<Map<String,Object>> queryTemplateReport();
}
