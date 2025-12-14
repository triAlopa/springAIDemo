package com.chen.exception.handle;

import com.chen.constant.ResultConstant;
import com.chen.exception.AccountBusinessException;
import com.chen.exception.AccountRegisterException;
import com.chen.exception.LoginException;
import com.chen.exception.ModelBusinessException;
import com.chen.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.chen.constant.ResultConstant.HTTPSTATUS.BAD_REQUEST;
import static com.chen.constant.ResultConstant.UNKNOWNMESSAGE;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {


    /**
     * 用户未登录访问暴露接口
     * @param e
     * @return
     */
    @ExceptionHandler(LoginException.class)
    public Result loginException(LoginException e) {

        log.warn("用户未登录: {}",e.getErrorMsg());

        return Result.fil(e.getErrorMsg(),e.getErrorCode());
    }

    /**
     * 用户注册错误
     * @param e
     * @return
     */
    @ExceptionHandler(AccountRegisterException.class)
    public Result accountRegister(AccountRegisterException e) {

        log.error("用户注册异常: {}",e.getErrorMsg());

        return Result.fil(e.getErrorMsg(),e.getErrorCode());
    }

    /**
     * 用户注册::不能重复注册
     * @param e
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result accountISRegister(DuplicateKeyException e) {

        log.error("用户注册异常:----》重复注册 {}",e.getMessage());

        int index = e.getMessage().indexOf(" for key ");

        String errMsg = e.getMessage().substring(index-20, index).split("'")[1];

        return Result.fil(errMsg,ResultConstant.HTTPSTATUS.REPEAT_REGISTER);
    }

    /**
     * 后端用户注册参数异常方法
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result accountRegisterWithFormErr(MethodArgumentNotValidException e) {

        log.error("用户注册参数表单异常: {}",e.getMessage());
        StringBuffer sb = new StringBuffer();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            sb.append(error.getDefaultMessage()+", ");
        });


        return Result.fil(sb.toString(),ResultConstant.HTTPSTATUS.REPEAT_REGISTER);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result accountRegisterWithFormErr(HandlerMethodValidationException e) {
        log.warn("用户表单出错: {}",e.getMessage());

        return Result.fil(UNKNOWNMESSAGE,BAD_REQUEST);
    }

    @ExceptionHandler(AccountBusinessException.class)
    public Result accountBusinessHandle(AccountBusinessException e) {
        log.warn("用户业务出错: {}",e.getErrorMsg());

        return Result.fil(e.getErrorMsg(),BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result unknownException(Exception e) {
        log.error("业务逻辑出错: {}",e.getMessage());
        return Result.fil(UNKNOWNMESSAGE,ResultConstant.HTTPSTATUS.UNKNOWN_ERROR);
    }

    /**
     * 用户已经申请完了所有的模型列表
     * @param e
     * @return
     */
    @ExceptionHandler(ModelBusinessException.class)
    public Result modelBusinessHandle(ModelBusinessException e) {

        log.warn("用户会话列表已经满了{}",e.getErrorCode());

        return Result.fil(e.getErrorMsg(),e.getErrorCode());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result resourceFoundException(NoResourceFoundException e) {

        log.info("找不到该资源：{}",e.getMessage());

        return Result.fil(e.getMessage(),HttpStatus.FOUND.value());
    }

}
