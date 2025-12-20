package com.chen.service;

import com.chen.pojo.entity.Company;
import com.chen.pojo.vo.ModelVO;

public interface ModelService {

    /**
     * 根据modelid查询hr以及公司信息
     * @param modelId
     * @return
     */
    ModelVO queryModelWithCompany(Integer modelId);

    void handelCompanyAddress(Company company);
}
