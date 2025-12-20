package com.chen.controller.user;

import com.chen.aspect.LogOperation;
import com.chen.pojo.Result;
import com.chen.pojo.dto.OfferDTO;
import com.chen.service.OfferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/offer")
@Slf4j
@Tag(name = "用户offer接口")
public class OfferController {

    @Resource
    private OfferService offerService;

    @PostMapping
    @LogOperation
    public Result responseUser4offer(@RequestBody OfferDTO offerDTO) {

        log.info("用户发出offer响应{}", offerDTO);

        offerService.handleUserRequest(offerDTO);

        return Result.success();
    }

}
