package com.chen.service;

import com.chen.pojo.PageResult;
import com.chen.pojo.dto.QueryCompanyDTO;
import com.chen.pojo.entity.Company;
import com.chen.pojo.vo.CompanyVO;

import java.util.List;

public interface CompanyService {

    PageResult<List<CompanyVO>> queryAllCompany(QueryCompanyDTO queryCompanyDTO);


    CompanyVO querySingleInfo(String companyId);
}
