package com.chen.mapper;

import com.chen.pojo.dto.QueryCompanyDTO;
import com.chen.pojo.entity.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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


    @Select("select tc.* from tb_ai_company tc,tb_ai_session ts,tb_ai_model tm" +
            " where ts.model_id=tm.model_id and tm.company_id=tc.company_id and ts.session_id=#{sessionId}")
    Company selectCompanyBySessionId(String sessionId);

    List<Company> queryCompany(QueryCompanyDTO queryCompanyDTO);

}




