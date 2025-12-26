package com.chen.controller.user;

import com.chen.aspect.LogOperation;
import com.chen.pojo.Result;
import com.chen.service.UserService;
import com.chen.util.CurrentUserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@Slf4j
@RequestMapping("/upload")
@Tag(name = "上传相关API", description = "管理端修改图片接口")
public class UploadController {

    @Autowired
    private UserService userService;

    @Operation(summary = "修改用户密码", description = "根据前端请求头携带token,获取用户的信息,修改用户密码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "原密码错误")
    })
    @PostMapping("/image")
    @LogOperation
    public Result<String> uploadUserImage(MultipartFile file) {

        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        log.info("用户：{}，尝试上传文件：{}", userId, file);

        String url = userService.uploadImage(file);

        return Result.success(url);
    }
}
