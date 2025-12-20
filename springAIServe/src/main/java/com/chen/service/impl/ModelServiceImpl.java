package com.chen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.chen.exception.ModelBusinessException;
import com.chen.mapper.CompanyMapper;
import com.chen.mapper.ModelMapper;
import com.chen.pojo.entity.Company;
import com.chen.pojo.entity.Model;
import com.chen.pojo.properties.TencentMapProperties;
import com.chen.pojo.vo.CompanyVO;
import com.chen.pojo.vo.ModelVO;
import com.chen.service.ModelService;
import com.chen.util.TencentMapUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.chen.constant.ModelConstant.MODEL_NO_COMPANY;
import static com.chen.constant.ModelConstant.MODEL_NO_EXIST;
import static com.chen.constant.ResultConstant.HTTPSTATUS.NOT_FOUND;
import static com.chen.constant.TencentConstant.TENCENT_API_KEY;
import static com.chen.constant.TencentConstant.TENCENT_LOCATION;

@Service
public class ModelServiceImpl implements ModelService {

    @Resource
    private ModelMapper modelMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private TencentMapProperties tencentMapProperties;

    /**
     * 根据modelid查询hr以及公司信息
     *
     * @param modelId
     * @return
     */
    @Override
    public ModelVO queryModelWithCompany(Integer modelId) {
        Model model = modelMapper.queryModelById(modelId);

        if (model == null) {
            throw new ModelBusinessException(MODEL_NO_EXIST, NOT_FOUND);
        }
        //查询并赋值给vo
        ModelVO modelVO = new ModelVO();
        BeanUtils.copyProperties(model, modelVO);
        //查询公司相关的信息
        String companyId = model.getCompanyId();
        Company company = companyMapper.selectCompanyId(companyId);
        //逻辑校验
        if (company == null) {
            throw new ModelBusinessException(MODEL_NO_COMPANY, NOT_FOUND);
        }
        //位置处理
        handelCompanyAddress(company);
        //赋值给vo
        CompanyVO companyVO = new CompanyVO();
        BeanUtils.copyProperties(company, companyVO,"employerBenefit","jobTag");

        List<String> benefits = StrUtil.split(company.getEmployerBenefit(), StrUtil.C_SPACE);
        companyVO.setEmployerBenefit(benefits);
        List<String> jobTags = StrUtil.split(company.getJobTag(), StrUtil.C_SPACE);
        companyVO.setJobTag(jobTags);
        modelVO.setCompany(companyVO);

        return modelVO;
    }

    //处理公司的经纬度
    @Override
    public void handelCompanyAddress(Company company){
        String site = company.getAddress();
        Map<String,String> map=new TreeMap<>();
        map.put(TENCENT_LOCATION,site);
        String apiKey = tencentMapProperties.getApiKey();
        map.put(TENCENT_API_KEY,apiKey);

        String address = TencentMapUtil.generateAddress(map, tencentMapProperties.getSecretKey());
        company.setAddress(address);
    }
}
