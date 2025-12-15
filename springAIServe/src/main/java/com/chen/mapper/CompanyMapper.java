package com.chen.mapper;

import com.chen.pojo.entity.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author Chen
* @description 针对表【tb_ai_company(公司表)】的数据库操作Mapper
* @createDate 2025-12-14 21:45:14
* @Entity com.chen.pojo.entity.Company
*/
@Mapper
public interface CompanyMapper {


    @Select("select * from tb_ai_company where company_id=#{companyId}")
    Company selectCompanyId(String companyId);
}




