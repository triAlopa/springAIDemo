package com.chen.mapper;

import com.chen.pojo.dto.QueryUserDTO;
import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
}
