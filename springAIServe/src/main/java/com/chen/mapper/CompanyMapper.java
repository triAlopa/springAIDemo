package com.chen.mapper;

import com.chen.pojo.dto.QueryCompanyDTO;
import com.chen.pojo.entity.Company;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    void update(Company company);

    @Insert("insert into tb_ai_company( company_id, name, type, " +
            "low_salary, high_salary, address, job_tag, job_desc, employer_benefit, " +
            " update_user_id)" +
            " VALUES(#{companyId}, #{name},#{type},#{lowSalary},#{highSalary}," +
            "#{address},#{jobTag},#{jobDesc},#{employerBenefit},#{updateUserId}) ")
    void insert(Company company);

    @Update("update  tb_ai_company set is_del =0 where  company_id=#{companyId}")
    void deleteCompanyById(String companyId);
}




