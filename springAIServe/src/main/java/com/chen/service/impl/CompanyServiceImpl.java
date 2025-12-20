package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chen.mapper.CompanyMapper;
import com.chen.mapper.ModelMapper;
import com.chen.pojo.PageResult;
import com.chen.pojo.dto.QueryCompanyDTO;
import com.chen.pojo.entity.Company;
import com.chen.pojo.entity.Model;
import com.chen.pojo.vo.CompanyVO;
import com.chen.pojo.vo.ModelVO;
import com.chen.service.CompanyService;
import com.chen.service.ModelService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    @Resource
    private ModelService modelService;

    @Resource
    private ModelMapper modelMapper;


    @Override
    @SneakyThrows
    public PageResult<List<CompanyVO>> queryAllCompany(QueryCompanyDTO queryCompanyDTO) {

        PageHelper.startPage(queryCompanyDTO.getPageNum(), queryCompanyDTO.getPageSize());

        Page<Company> companyList = (Page<Company>) companyMapper.queryCompany(queryCompanyDTO);

        List<CompanyVO> list = companyList.getResult()
                .stream()
                .map(company -> {
                    //处理地址
                    modelService.handelCompanyAddress(company);
                    return BeanUtil.copyProperties(company, CompanyVO.class);
                }).toList();

        return new PageResult<>(list, companyList.getTotal());
    }

    @Override
    public CompanyVO querySingleInfo(String companyId) {

        Company company = companyMapper.selectCompanyId(companyId);
        modelService.handelCompanyAddress(company);

        CompanyVO companyVO = BeanUtil.copyProperties(company, CompanyVO.class);

        List<ModelVO> list = modelMapper.selectByCompanyId(companyId)
                .stream().map(model -> BeanUtil.copyProperties(model, ModelVO.class)).toList();


        companyVO.setModels(list);

        return companyVO;
    }


}
