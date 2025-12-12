package com.chen.mapper;

import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {


    @Insert("insert into tb_ai_user(nick_name, gender, birthday, password, " +
            "email, points, enable, is_del, register_time)" +
            " value (#{nickName},#{gender},#{birthday},#{password},#{email}," +
            "#{points},#{enable},#{isDel},#{registerTime})")
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
     void insert(User user);


    @Select("select * from tb_ai_user where email=#{email}")
    User selectByEmail(String email);

    User selectById(Integer userId);

    void updateSingleUser(UserChangePassDTO changePassDTO);
}
