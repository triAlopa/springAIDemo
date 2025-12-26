package com.chen.constant;

public class RedisConstant {

    public static final String USER_REGISTER = "user:register:";
    public static final long USER_REGISTER_TTL = 1000 * 60 * 2;

    public static final String USER_LOGIN = "user:login:";

    public static final String USER_CACHE_SESSION = "user:cache:session:";
    public static final long USER_CACHE_SESSION_TTL = 1000 * 60 * 60;

    public static final String USER_TOKEN = "user:token:";
    public static final long USER_TOKEN_TTL = 10800000;

  /*  public static final String USER_CACHE_MESSAGE="user:cache:message:";
    public static final long USER_CACHE_MESSAGE_TTL = 1000*60*60;*/
}
