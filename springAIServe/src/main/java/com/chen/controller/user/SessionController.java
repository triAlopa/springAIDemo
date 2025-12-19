package com.chen.controller.user;

import com.chen.pojo.Result;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.vo.AISessionVO;
import com.chen.pojo.vo.ModelVO;
import com.chen.service.SessionService;
import com.chen.util.CurrentUserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/ai/session")
@Tag(name = "用户会话",description = "用户相关会话接口")
public class SessionController {

    @Autowired
    private SessionService sessionService;


    @Operation(summary = "获取用户会话信息", description = "根据前端请求头携带token,用户会话信息,返回前端展示")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "获取失败")
    })
    @GetMapping
    public Result<List<AISessionVO>> getUserSession() {
        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        log.info("获取用户:{} 的会话信息", userId);

        List<AISessionVO> list = sessionService.queryUserSession(userId);

        return Result.success(list);
    }

    @Operation(summary = "删除用户会话信息", description = "根据前端session_id,用户会话信息,返回前端展示")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "删除失败")
    })
    @DeleteMapping("/{session_id}")
    public Result delUserSession(@NotNull @PathVariable("session_id") String sessionId) {
        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        log.info("{}用户请求删除: {}的会话信息", userId,sessionId);

        sessionService.delUserSession(userId,sessionId);

        return Result.success();
    }

    @Operation(summary = "创建用户会话信息", description = "根据前端请求头携带token,创建用户会话,返回前端展示")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "500", description = "创建失败")
    })
    @PostMapping
    public Result<ModelVO> createUserSession(@RequestBody AISessionDTO session) {

        log.info("创建新:{}会话", session);

        ModelVO modelVO =sessionService.createUserSession(session);

        return Result.success(modelVO);
    }
}
