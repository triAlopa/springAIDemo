package com.chen.constant;

public class ResultConstant {

    public  class  HTTPSTATUS{
        //成功 200
        public static final int SUCCESS = 200;
        //业务服务端错误
        public static final int UNKNOWNERROR = 500;
        //用户未授权
        public static final int UNAUTHORIZED = 401;
        //客服端请求语法错误
        public static final int BADREQUEST = 400;
        //用户重复注册
        public static final int REPEATREGISTER = 409;

    }

    public static final String UNKNOWNMESSAGE ="未知错误,请重试" ;
    public static final String SUCCESS ="请求成功" ;
    public static final String UNAUTHMSG ="用户未授权登录" ;
    public static final String REGISTERERROR ="用户注册请求出错" ;
    public static final String USER_IEXIST_EMAIL="用户输入邮箱不存在" ;
    public static final String USER_INPUT_PASS_ERR="用户输入邮箱不存在" ;


}
