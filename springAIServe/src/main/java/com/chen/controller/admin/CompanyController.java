package com.chen.controller.admin;

import com.chen.pojo.PageResult;
import com.chen.pojo.Result;
import com.chen.pojo.dto.QueryCompanyDTO;
import com.chen.pojo.vo.CompanyVO;
import com.chen.pojo.vo.UserVO;
import com.chen.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("adminCompanyController")
@Slf4j
@RequestMapping("/admin/company")
@Tag(name = "admin端用户接口", description = "公司管理接口")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }


    @GetMapping
    public Result<PageResult<List<CompanyVO>>> queryAllCompany(QueryCompanyDTO queryCompanyDTO) {

        log.info("请求查询用户,参数为{}", queryCompanyDTO);

        PageResult<List<CompanyVO>> list = companyService.queryAllCompany(queryCompanyDTO);

        return Result.success(list);

    }

    @GetMapping("/single")
    private Result<CompanyVO> querySingleCompany(@RequestParam String companyId) {

        log.info("请求查询公司id为：{}的详细信息", companyId);

        CompanyVO companyVO =companyService.querySingleInfo(companyId);

        return Result.success(companyVO);
    }

}
