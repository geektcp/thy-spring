package com.geektcp.common.spring.context;

/**
 * @author Mr.Tang on 2020/11/11 17:23.
 */
public class BaseContextConstant {

    private BaseContextConstant() {
    }

    public static final String JWT_KEY_USER_ID = "user_id";
    public static final String JWT_KEY_TOKEN_TYPE = "token_type";
    public static final String JWT_KEY_CLIENT_ID = "client_id";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String BEARER_HEADER_KEY = "token";
    public static final String BEARER_HEADER_PREFIX = "Bearer ";
    public static final String BEARER_HEADER_PREFIX_EXT = "Bearer%20";
    public static final String BASIC_HEADER_KEY = "Authorization";
    public static final String BASIC_HEADER_PREFIX = "Basic ";
    public static final String BASIC_HEADER_PREFIX_EXT = "Basic%20";
    public static final String IS_BOOT = "boot";
    public static final String TRACE_ID_HEADER = "x-trace-header";
    public static final String LOG_TRACE_ID = "trace";

}
