package com.chen.constant;

public class UserConstant {
    public static final String EMAIL = "email";
    public static final String NICKNAME = "nickName";

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

    /**
     * 注册或者登录校验错误字段
     */
    public class RegisterOrLOGIN{
        //TODO INVALID
        public static final  String nickNameUNVALID= "用户名称不能为空!";
        public static final  String genderUNVALID= "性别不能为空!";
        public static final  String passwordUNVALID= "用户名密码不能为空!";
        public static final  String emailUNVALID= "邮箱不能为空!";
        public static final  String birthdayNVALID= "生日不能为空!";
        public static final  String emailUNVALIDWITHREGX= "邮箱格式不正确！";

        public static final  String UNREGISTER= "不能重复注册!";
        public static final  String UNCODE= "验证码失效或者未发送成功，请重新发送";

    }

}
