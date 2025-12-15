package com.chen.controller;

import com.chen.pojo.Result;
import com.chen.pojo.vo.ModelVO;
import com.chen.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model")
@Slf4j
@Tag(name = "hr管理")
public class ModelController {

    @Resource
    private ModelService modelService;

    @Operation(summary = "用户查询聊天窗口对应的hr及公司信息")
    @GetMapping("/user")
    public Result<ModelVO> queryModelWithCompany(@RequestParam Integer modelId){

        log.info("用户查询 Id为:{}的hr", modelId);

        ModelVO model=modelService.queryModelWithCompany(modelId);

        return Result.success(model);
    }

}
