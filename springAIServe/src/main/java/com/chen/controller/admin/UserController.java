package com.chen.controller.admin;

import com.chen.pojo.PageResult;
import com.chen.pojo.Result;
import com.chen.pojo.dto.QueryUserDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.User;
import com.chen.pojo.vo.UserVO;
import com.chen.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminUserController")
@Slf4j
@RequestMapping("/admin/user")
@Tag(name = "admin端用户接口", description = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "查询所有用户", description = "根据前端请求头携带token,获取所有用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "404", description = "没有")
    })
    @GetMapping("/queryAll")
    public Result<PageResult<List<UserVO>>> queryAllUser(QueryUserDTO queryUserDTO) {

        log.info("请求查询用户,参数为{}", queryUserDTO);

        PageResult<List<UserVO>> list = userService.queryAllUser(queryUserDTO);

        return Result.success(list);
    }

    @Operation(summary = "修改用户", description = "根据前端请求头携带token,修改用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "没有")
    })
    @PutMapping()
    public Result updateORSaveSingleUser(@Validated(UserDTO.onAdmin.class) @RequestBody @Schema UserDTO user) {

        log.info("请求 用户,参数为{}", user);

        userService.updateSingleUser(user);

        return Result.success();
    }

    @Operation(summary = "查询用户", description = "查询用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "没有")
    })
    @GetMapping("/single")
    public Result<UserVO> queryUser(@RequestParam Integer id) {

        log.info("修改回显{}", id);

        UserVO user= userService.selectById(id);

        return Result.success(user);
    }

    @Operation(summary = "删除用户", description = "删除用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "没有")
    })
    @DeleteMapping("/del/{userId}")
    public Result delUser(@PathVariable("userId") Integer id) {

        log.info("逻辑删除用户{}", id);

        userService.deleteUserByLogical(id);

        return Result.success();
    }

    @Operation(summary = "删除用户", description = "删除用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "没有")
    })
    @DeleteMapping("/del")
    public Result batchDelUser( @RequestParam List<Integer> ids) {

        log.info("逻辑批量删除用户{}", ids);

        userService.deleteUsersByLogical(ids);

        return Result.success();
    }
}
