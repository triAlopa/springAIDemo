package com.chen.exception.handle;

import com.chen.constant.ResultConstant;
import com.chen.exception.AccountRegisterException;
import com.chen.exception.LoginException;
import com.chen.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {


    /**
     * 用户未登录访问暴露接口
     * @param e
     * @return
     */
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result loginException(LoginException e) {

        log.error("用户未登录: {}",e.getMessage());

        return Result.fil(ResultConstant.UNAUTHMSG,ResultConstant.HTTPSTATUS.UNAUTHORIZED);
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

        return Result.fil(errMsg,ResultConstant.HTTPSTATUS.REPEATREGISTER);
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


        return Result.fil(sb.toString(),ResultConstant.HTTPSTATUS.REPEATREGISTER);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result unknownException(Exception e) {
        log.error("业务逻辑出错: {}",e.getMessage());
        return Result.fil(ResultConstant.UNKNOWNMESSAGE,ResultConstant.HTTPSTATUS.UNKNOWNERROR);
    }

}
