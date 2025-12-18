package com.chen.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.chen.exception.AccountRegisterException;
import com.chen.mapper.AISessionMapper;
import com.chen.mapper.CompanyMapper;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.dto.OfferDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.Company;
import com.chen.service.OfferService;
import com.chen.util.CurrentUserHolder;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.Objects;

import static com.chen.constant.SystemConstant.Offer_type.ACCEPT;
import static com.chen.constant.UserConstant.DISABLE;

@Service
@Slf4j
public class OfferServiceImpl implements OfferService {

    @Autowired
    private AISessionMapper sessionMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private ChatClient chatClient;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private ModelServiceImpl modelServiceImpl;

    @Override
    public void handleUserRequest(OfferDTO offerDTO) {
        Integer type = offerDTO.getType();
        //用户答应了offer
        if (Objects.equals(type, ACCEPT)) {
            handleInsertOrSend(offerDTO);
        }
    }

    private void handleInsertOrSend(OfferDTO offerDTO) {
        //更新会话状态为禁用

        UserDTO userDTO = CurrentUserHolder.getCurrentUser();
        AISessionDTO sessionDTO = AISessionDTO.builder()
                .userId(userDTO.getId())
                .sessionId(offerDTO.getSessionId())
                .enable(DISABLE)
                .build();

        sessionMapper.updateSingleSession(sessionDTO);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            //发送者
            mimeMessageHelper.setFrom(from);

            //为邮箱html变量赋值
            //设置html处理变量
            Context context = new Context();
            Company company = companyMapper.selectCompanyBySessionId(sessionDTO.getSessionId());
            context.setVariable("nickName", userDTO.getNickName());
            context.setVariable("companyName", company.getName());
            //能实现就行了 不管了😅
            modelServiceImpl.handelCompanyAddress(company);
            context.setVariable("companyAddress", company.getAddress());
            context.setVariable("currentYear", LocalDate.now().getYear());
            context.setVariable("jobTag", company.getJobTag());

            String checkSalary = """
                分析用户请求。
                用户这次说的话：%s
                只需返回用户答应的薪资，没有记录谈到就返回最大值
                """.formatted("答应了offer");

            String salary = chatClient.prompt(checkSalary).advisors(
                    chat -> chat.param(ChatMemory.CONVERSATION_ID, offerDTO.getSessionId())
            ).call().content();
            context.setVariable("salary",salary);
            //处理为字符串发送
            String template = templateEngine.process("SendOfferTemplate.html", context);
            //确认发送文本
            mimeMessageHelper.setText(template, true);
            mimeMessage.setSubject("boss求聘平台"+company.getName()+"offer");
            //收件人
            String email=userDTO.getEmail();
            mimeMessageHelper.setTo(email);
            //发送
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("send email code error", e);
            throw new RuntimeException(e);
        }
    }
}
