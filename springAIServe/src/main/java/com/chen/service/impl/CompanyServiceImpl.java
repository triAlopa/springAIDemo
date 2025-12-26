package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.chen.mapper.CompanyMapper;
import com.chen.mapper.ModelMapper;
import com.chen.pojo.PageResult;
import com.chen.pojo.dto.QueryCompanyDTO;
import com.chen.pojo.entity.Company;
import com.chen.pojo.entity.Model;
import com.chen.pojo.properties.TencentMapProperties;
import com.chen.pojo.vo.CompanyVO;
import com.chen.pojo.vo.ModelVO;
import com.chen.service.CompanyService;
import com.chen.service.ModelService;
import com.chen.util.CurrentUserHolder;
import com.chen.util.TencentMapUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

import static com.chen.constant.TencentConstant.*;
import static com.chen.constant.UserConstant.ENABLE;
import static com.chen.constant.UserConstant.NODEL;

@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {



    @Autowired
    public CompanyServiceImpl(CompanyMapper companyMapper,
                              ModelService modelService,
                              ModelMapper modelMapper,
                              SpringTemplateEngine springTemplateEngine,
                              TencentMapProperties tencentMapProperties) {
        this.companyMapper = companyMapper;
        this.modelService = modelService;
        this.modelMapper = modelMapper;
        this.springTemplateEngine = springTemplateEngine;
        this.tencentMapProperties = tencentMapProperties;
    }


    private final CompanyMapper companyMapper;

    private final ModelService modelService;

    private final ModelMapper modelMapper;

    private SpringTemplateEngine springTemplateEngine;

    private TencentMapProperties tencentMapProperties;



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
                    CompanyVO bean= BeanUtil.copyProperties(company, CompanyVO.class);
                    handleTag(company, bean);
                    return bean;
                }).toList();

        return new PageResult<>(list, companyList.getTotal());
    }

    @Override
    public CompanyVO querySingleInfo(String companyId) {

        Company company = companyMapper.selectCompanyId(companyId);
        modelService.handelCompanyAddress(company);

        CompanyVO companyVO = BeanUtil.copyProperties(company, CompanyVO.class);

        handleTag(company, companyVO);


        List<ModelVO> list = modelMapper.selectByCompanyId(companyId)
                .stream().map(model -> BeanUtil.copyProperties(model, ModelVO.class)).toList();


        companyVO.setModels(list);

        return companyVO;
    }

    private static void handleTag(Company company, CompanyVO companyVO) {
        List<String> benefits = StrUtil.split(company.getEmployerBenefit(), StrUtil.C_SPACE);
        companyVO.setEmployerBenefit(benefits);
        List<String> jobTags = StrUtil.split(company.getJobTag(), StrUtil.C_SPACE);
        companyVO.setJobTag(jobTags);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompanyWithModels(CompanyVO companyVO) {

        Company company = new Company();

        BeanUtil.copyProperties(companyVO, company);
        company.setJobTag(StrUtil.join(" ", companyVO.getJobTag()));
        company.setEmployerBenefit(StrUtil.join(" ", companyVO.getEmployerBenefit()));

        handleCompanyParseAddress(company);

        companyMapper.update(company);

        if(companyVO.getModels()==null||companyVO.getModels().isEmpty()){
            return;
        }

        List<String> ids = companyVO.getModels()
                .stream()
                .map(ModelVO::getModelId)
                .toList();
        modelMapper.delByLogical(ids);



        List<Model> list = companyVO.getModels().stream()
                .map(bean -> {
                    return batchHandleModelInfo(bean, company);
                }).toList();

        list.forEach(item -> {
            handleModelsDescription(item, company);
        });

        modelMapper.batchInsertModel(list);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCompanyWithModels(CompanyVO companyVO) {
        Company company = new Company();

        BeanUtil.copyProperties(companyVO, company);
        company.setJobTag(StrUtil.join(" ", companyVO.getJobTag()));
        company.setEmployerBenefit(StrUtil.join(" ", companyVO.getEmployerBenefit()));
        company.setCompanyId("COMP_"+UUID.randomUUID().toString(true).substring(0,5));
        companyMapper.insert(company);

        if(companyVO.getModels()==null||companyVO.getModels().isEmpty()){
            return;
        }

        List<Model> list = companyVO.getModels().stream()
                .map(bean -> {
                    Model model = BeanUtil.copyProperties(bean, Model.class);
                    handleModelsDescription(model, company);
                    String modelId="Model_"+company.getCompanyId()+"_"+ UUID.randomUUID().toString(true).substring(0,5);
                    model.setModelId(modelId);
                    model.setCompanyId(company.getCompanyId());
                    model.setUpdateUserId(CurrentUserHolder.getCurrentUser().getId());
                    model.setUpdateTime(LocalDateTime.now());
                    model.setIsDel(NODEL);
                    model.setCreatedTime(LocalDateTime.now());
                    return model;
                }).toList();

        handleCompanyParseAddress(company);
        modelMapper.batchInsertModel(list);


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delCompanyWithModels(String companyId) {

        companyMapper.deleteCompanyById(companyId);

        List<String> list = modelMapper.selectByCompanyId(companyId)
                .stream().map(Model::getModelId).toList();

        if(list.isEmpty()) return;

        modelMapper.delByLogical(list);

    }

    private static Model batchHandleModelInfo(ModelVO bean, Company company) {
        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        Model model = BeanUtil.copyProperties(bean, Model.class);

        String modelId="Model_"+company.getCompanyId()+"_"+ UUID.randomUUID().toString(true).substring(0,5);

        model.setModelId(modelId);
        model.setCompanyId(company.getCompanyId());
        model.setIsDel(NODEL);
        model.setUpdateTime(LocalDateTime.now());
        model.setUpdateUserId(userId);
        return model;
    }

    private void handleCompanyParseAddress(Company company) {
        String address = company.getAddress();
        Map<String, String> map = new TreeMap<>();
        map.put(TENCENT_ADDRESS, address);
        String apiKey = tencentMapProperties.getApiKey();
        map.put(TENCENT_API_KEY, apiKey);
        String point = TencentMapUtil.parseAddress(map, tencentMapProperties.getSecretKey());
        company.setAddress(point);
    }


    private void handleModelsDescription(Model model, Company company) {

        Context ctx = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("modelName", model.getName());
        if (model.getTemperature() >= 0.0 && model.getTemperature() <= 0.7) {
            data.put("modelPersonality", "强硬型");
        } else if (model.getTemperature() > 0.7 && model.getTemperature() <= 1.5) {
            data.put("modelPersonality", "幽默型");
        } else if (model.getTemperature() > 1.5 && model.getTemperature() <= 2.0) {
            data.put("modelPersonality", "卑微型");
        }
        data.put("modelCompanyName", company.getName());
        data.put("modelCompanyAddress", company.getAddress());
        data.put("modelCompanyLowSalary", company.getLowSalary());
        data.put("modelCompanyHighSalary", company.getHighSalary());
        data.put("modelCompanyJobTag", company.getJobTag());
        data.put("modelCompanyBenefits", company.getEmployerBenefit());
        ctx.setVariables(data);
        String description = springTemplateEngine.process("text/ModelPrompt.txt", ctx);
        model.setDescription(description);

    }
}
