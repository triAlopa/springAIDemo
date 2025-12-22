package com.chen.aspect;

import cn.hutool.core.util.StrUtil;
import com.chen.mapper.OperateLogMapper;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.OperateLog;
import com.chen.service.UserService;
import com.chen.util.CurrentUserHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogAspect   {

    @Autowired
    private OperateLogMapper operateLogMapper;



    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint,  LogOperation log) throws Throwable {

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        // 执行方法
        Object result = joinPoint.proceed();
        // 当前时间
        long endTime = System.currentTimeMillis();
        // 耗时
        long costTime = endTime - startTime;

        // 构建日志对象
        OperateLog operateLog = new OperateLog();
        UserDTO userDTO = CurrentUserHolder.getCurrentUser();
        operateLog.setOperateId(userDTO !=null ? userDTO.getId() : null); // 需要实现 getCurrentUserId 方法
        operateLog.setOperateTime(LocalDateTime.now());
        operateLog.setClassName(joinPoint.getTarget().getClass().getName());
        operateLog.setMethodName(joinPoint.getSignature().getName());
        operateLog.setMethodParams(Arrays.toString(joinPoint.getArgs()));
        if (result!= null&&!"".equals(result.toString())) {
            if(result.toString().length()>2000) operateLog.setReturnValue(result.toString().substring(0,2000));
            else operateLog.setReturnValue(result.toString());
        }


        operateLog.setCostTime(costTime);


        System.out.println(operateLog);

        // 插入日志
        operateLogMapper.insert(operateLog);

        return result;
    }
}
