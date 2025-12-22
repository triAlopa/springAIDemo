package com.chen.controller.admin;

import com.chen.aspect.LogOperation;
import com.chen.pojo.PageResult;
import com.chen.pojo.Result;
import com.chen.pojo.dto.QueryUserDTO;
import com.chen.pojo.entity.OperateLog;
import com.chen.pojo.vo.UserVO;
import com.chen.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "报表统计")
@RequestMapping("/admin/log")
public class LogController {


    @Autowired
    private LogService logService;


    @LogOperation
    @Operation(summary = "查询日志", description = "根据前端请求头携带token,获取所有用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "404", description = "没有")
    })
    @GetMapping("/queryAll")
    public Result<PageResult<List<OperateLog>>> queryLog(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {

        log.info("请求查询日志,参数为{},{}", pageNum , pageSize);

        PageResult<List<OperateLog>> list = logService.queryLog(pageNum,pageSize);

        return Result.success(list);
    }
}
