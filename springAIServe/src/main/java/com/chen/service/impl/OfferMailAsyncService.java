package com.chen.service.impl;

import com.chen.mapper.AISessionMapper;
import com.chen.mapper.CompanyMapper;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.dto.OfferDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static com.chen.constant.UserConstant.DISABLE;

@Service
@Slf4j
public class OfferMailAsyncService {

    private final AISessionMapper sessionMapper;
    private final CompanyMapper companyMapper;
    private final ChatClient chatClient;
    private final TemplateEngine templateEngine;
    private final ModelServiceImpl modelServiceImpl;
    private final OfferMailRetryService offerMailRetryService;

    public OfferMailAsyncService(AISessionMapper sessionMapper,
                                 CompanyMapper companyMapper,
                                 ChatClient chatClient,
                                 TemplateEngine templateEngine,
                                 ModelServiceImpl modelServiceImpl,
                                 OfferMailRetryService offerMailRetryService) {
        this.sessionMapper = sessionMapper;
        this.companyMapper = companyMapper;
        this.chatClient = chatClient;
        this.templateEngine = templateEngine;
        this.modelServiceImpl = modelServiceImpl;
        this.offerMailRetryService = offerMailRetryService;
    }

    @Async("offerMailExecutor")
    public CompletableFuture<Void> sendOfferEmailAsync(OfferDTO offerDTO, UserDTO userDTO) {
        try {
            Company company = companyMapper.selectCompanyBySessionId(offerDTO.getSessionId());
            if (company == null) {
                log.warn("未找到对应公司信息，无法发送offer邮件, sessionId={}", offerDTO.getSessionId());
                return CompletableFuture.completedFuture(null);
            }

            Context context = new Context();
            context.setVariable("nickName", userDTO.getNickName());
            context.setVariable("companyName", company.getName());
            modelServiceImpl.handelCompanyAddress(company);
            context.setVariable("companyAddress", company.getAddress());
            context.setVariable("currentYear", LocalDate.now().getYear());
            context.setVariable("jobTag", company.getJobTag());
            context.setVariable("salary", querySalary(offerDTO.getSessionId()));

            String template = templateEngine.process("SendOfferTemplate.html", context);
            String subject = "boss求聘平台" + company.getName() + "offer";
            boolean sendSuccess = offerMailRetryService.sendHtmlMail(userDTO.getEmail(), subject, template);
            if (sendSuccess) {
                disableSession(offerDTO, userDTO);
                log.info("offer邮件发送成功, sessionId={}, userId={}", offerDTO.getSessionId(), userDTO.getId());
            }
        } catch (Exception e) {
            log.error("异步发送offer邮件失败, sessionId={}, userId={}", offerDTO.getSessionId(), userDTO.getId(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    private String querySalary(String sessionId) {
        String checkSalary = """
                分析用户请求。
                用户这次说的话：%s
                只需返回用户答应的薪资，没有记录谈到就返回最大值
                """.formatted("答应了offer");

        return chatClient.prompt(checkSalary)
                .advisors(chat -> chat.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();
    }

    private void disableSession(OfferDTO offerDTO, UserDTO userDTO) {
        AISessionDTO sessionDTO = AISessionDTO.builder()
                .userId(userDTO.getId())
                .sessionId(offerDTO.getSessionId())
                .enable(DISABLE)
                .build();
        sessionMapper.updateSingleSession(sessionDTO);
    }
}
