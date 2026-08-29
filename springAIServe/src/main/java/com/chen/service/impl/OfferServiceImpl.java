package com.chen.service.impl;

import com.chen.exception.NormalBusinessException;
import com.chen.pojo.dto.OfferDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.service.OfferService;
import com.chen.util.CurrentUserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.chen.constant.SystemConstant.Offer_type.ACCEPT;

@Service
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final OfferMailAsyncService offerMailAsyncService;

    public OfferServiceImpl(OfferMailAsyncService offerMailAsyncService) {
        this.offerMailAsyncService = offerMailAsyncService;
    }

    @Override
    public void handleUserRequest(OfferDTO offerDTO) {
        Integer type = offerDTO.getType();
        UserDTO userDTO = CurrentUserHolder.getCurrentUser();
        if (userDTO == null) {
            throw new NormalBusinessException("请重新登录后再操作offer");
        }

        if (Objects.equals(type, ACCEPT)) {
            offerMailAsyncService.sendOfferEmailAsync(offerDTO, userDTO);
        }
        log.info("offer请求已受理, type={}, sessionId={}, userId={}", type, offerDTO.getSessionId(), userDTO.getId());
    }
}
