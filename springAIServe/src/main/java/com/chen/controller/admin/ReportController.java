package com.chen.controller.admin;

import com.chen.pojo.Result;
import com.chen.pojo.vo.report.EmailReportVO;
import com.chen.pojo.vo.report.UserReportVO;
import com.chen.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@Tag(name = "报表统计")
@RequestMapping("/admin/report")
public class ReportController {


    @Autowired
    private UserService userService;


    @Operation(summary = "报表统计用户", description = "报表统计用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "没有")
    })
    @GetMapping("/userInfo")
    public Result<List> queryUserRegisterInfo(@RequestParam String timeStamp) {

        LocalDate parse = LocalDate.parse(timeStamp, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime start=LocalDateTime.of(parse.getYear(), parse.getMonth(), parse.getDayOfMonth(), 0, 0, 0);

        log.info("报表统计用户 时间：{}", start.toString());

        List list = userService.queryUserRegisterInfo(start);

        return Result.success(list);
    }

    /**
     * 获取邮箱分布占比
     */
    @GetMapping("/email")
    public Result<List<EmailReportVO>> getEmailStats() {

        log.info("获取用户邮箱信息");

        List<EmailReportVO> list = userService.getEmailStats();

        return Result.success(list);
    }

    /**
     * 获取邮箱分布占比
     */
    @PostMapping()
    public void requestUserReportExcel(HttpServletResponse response) {

        log.info("请求报表信息");

        userService.getUserReportExcel(response);

    }
}
