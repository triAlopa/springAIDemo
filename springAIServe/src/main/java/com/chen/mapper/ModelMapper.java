package com.chen.mapper;

import com.chen.pojo.entity.Model;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author Chen
* @description 针对表【tb_ai_model(hr表)】的数据库操作Mapper
* @createDate 2025-12-14 21:45:14
* @Entity com.chen.pojo.entity.Model
*/
@Mapper
public interface ModelMapper {

    @Select("select  model_id, company_id, name, description, " +
            "image, temperature, open_message, enable, is_del," +
            " remark, created_time from tb_ai_model where is_del=1 and enable=1")
    List<Model> queryAllModel();

    @Select("select  * from tb_ai_model where model_id=#{modelId}")
    Model queryModelById(Integer modelId);

    @Select("select * from  tb_ai_model")
    List<Model> queryAllModels();

    @Update("update tb_ai_model set description=#{description}    where model_id=#{modelId}")
    void update(Model model);

    @Select("select  * from tb_ai_model where company_id=#{companyId}")
    List<Model> selectByCompanyId(String companyId);
 }




