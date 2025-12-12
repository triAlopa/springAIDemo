package com.chen.constant;

public class UserConstant {
    public static final String EMAIL = "email";
    public static final String NICKNAME = "nickName";
    public static final String USER_ID="userId";

    /**
     * 1.用户账号状态有效
     * 0.无效
     */
    public static final int ENABLE = 1;
    public static final int DISABLE = 0;

    /**
     * 1.没删除
     * 0.逻辑删除
     */
    public static final int NODEL = 1;
    public static final int ISDEL = 0;

    public static final int DEFAULT_POINTS = 1000;

    public static final String OLD_PASSWORD_ERR="原来的密码错误";

    /**
     * 注册或者登录校验错误字段
     */
    public class RegisterOrLOGIN{
        public static final  String NICKNAME_INVALID= "用户名称不能为空!";
        public static final  String GENDER_INVALID= "性别不能为空!";
        public static final  String PASSWORD_INVALID= "用户名密码不能为空!";
        public static final  String EMAIL_INVALID= "邮箱不能为空!";
        public static final  String EMAIL_CODE_INVALID= "邮箱验证码不能为空!";
        public static final  String BIRTHDAY_INVALID= "生日不能为空!";
        public static final  String EMAIL_INVALID_WITH_REGX= "邮箱格式不正确！";

        public static final  String UNREGISTER= "不能重复注册!";
        public static final  String UNCODE= "验证码失效或者未发送成功，请重新发送";
        public static final  String INPUT_CODE_ERR= "验证码错误";
        public static final  String REPEAT_REQUEST_CODE= "不要重复请求验证码!";
        public static final  String INPUT_PARAMS_ERR="非法操作,请完善表单";



    }

}
