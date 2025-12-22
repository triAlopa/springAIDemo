package com.chen.controller.admin;

import com.chen.aspect.LogOperation;
import com.chen.pojo.PageResult;
import com.chen.pojo.Result;
import com.chen.pojo.dto.QueryCompanyDTO;
import com.chen.pojo.vo.CompanyVO;
import com.chen.pojo.vo.UserVO;
import com.chen.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @LogOperation
    public Result<PageResult<List<CompanyVO>>> queryAllCompany(QueryCompanyDTO queryCompanyDTO) {

        log.info("请求查询用户,参数为{}", queryCompanyDTO);

        PageResult<List<CompanyVO>> list = companyService.queryAllCompany(queryCompanyDTO);

        return Result.success(list);

    }

    @GetMapping("/single")
    @LogOperation
    private Result<CompanyVO> querySingleCompany(@RequestParam String companyId) {

        log.info("请求查询公司id为：{}的详细信息", companyId);

        CompanyVO companyVO =companyService.querySingleInfo(companyId);

        return Result.success(companyVO);
    }

    @PutMapping
    @LogOperation
    public Result updateCompanyWithModels(@RequestBody CompanyVO companyVO) {

        log.info("请求修改公司以及hr,参数为{}", companyVO);

        companyService.updateCompanyWithModels(companyVO);

        return Result.success();

    }

    @PostMapping
    @LogOperation
    public Result insertCompanyWithModels(@RequestBody CompanyVO companyVO) {

        log.info("请求添加公司以及hr,参数为{}", companyVO);

        companyService.insertCompanyWithModels(companyVO);

        return Result.success();

    }

    @DeleteMapping
    @LogOperation
    public Result delCompanyWithModels(@RequestParam String companyId) {

        log.info("请求删除公司以及hr,参数为{}", companyId);

        companyService.delCompanyWithModels(companyId);

        return Result.success();

    }

}
