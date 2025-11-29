package com.chen.mapper;

import com.chen.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {


    @Insert("insert into tb_ai_user(nick_name, gender, birthday, password, " +
            "email, points, enable, is_del, register_time)" +
            " value (#{nickName},#{gender},#{birthday},#{password},#{email}," +
            "#{points},#{enable},#{isDel},#{registerTime})")
     void insert(User user);


    @Select("select * from tb_ai_user where email=#{email}")
    User selectByEmail(String email);
}
