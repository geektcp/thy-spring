package com.geektcp.common.spring.util;

import com.geektcp.common.mosheh.system.Sys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class HttpRequestHeadUtils {

    private HttpRequestHeadUtils() {
    }

    public static final String HEAD_TOKEN = "Token";
    public static final String HEAD_COOKIE = "Cookie";
    public static final String HEAD_AUTHORIZATION = "Authorization";
    public static final String HEAD_USER_ID = "User-Id";
    public static final String HEAD_TENANT_ID = "Tenant-Id";
    public static final String HEAD_USER_TYPE = "User-Type";
    public static final String HEAD_NAME = "Name";
    public static final String HEAD_IP = "Ip";

    public static boolean carryToken() {
        return StringUtils.isNotEmpty(getValueByKey(HEAD_TOKEN));
    }

    public static String getToken() {
        return getValueByKey(HEAD_TOKEN);
    }

    public static boolean setToken(String token) {
        return setValueByKey(HEAD_TOKEN, token);
    }

    public static String getAuthorization() {
        String authorization = getValueByKey(HEAD_AUTHORIZATION);
        if(Objects.isNull(authorization)){
            return null;
        }
        return new String(Base64Utils.decodeFromString(authorization));
    }

    public static boolean setAuthorization(String authorization) {
        return setValueByKey(HEAD_AUTHORIZATION, authorization);
    }

    public static String getCookie() {
        return getValueByKey(HEAD_COOKIE);
    }

    public static boolean setCookie(String cookie) {
        return setValueByKey(HEAD_COOKIE, cookie);
    }

    public static String getUserID() {
        return getValueByKey(HEAD_USER_ID);
    }

    public static String getUserType() {
        return getValueByKey(HEAD_USER_TYPE);
    }

    public static String getTenantId() {
        return getValueByKey(HEAD_TENANT_ID);
    }

    public static boolean setTenantId(String tenantId) {
        return setValueByKey(HEAD_TENANT_ID, tenantId);
    }

    public static String getName() {
        return getValueByKey(HEAD_NAME);
    }

    public static boolean setName(String name) {
        return setValueByKey(HEAD_NAME, name);
    }

    public static String getIp() {
        return getValueByKey(HEAD_IP);
    }

    public static boolean setIp(String ip) {
        return setValueByKey(HEAD_IP, ip);
    }


    public static String getValueByKey(String key) {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(requestAttributes)) {
                return null;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            Object header = request.getHeader(key);
            if (Objects.nonNull(header)) {
                return header.toString();
            }
            Object attribute = request.getAttribute(key);
            if (Objects.nonNull(attribute)) {
                return attribute.toString();
            }
            return "";
        } catch (Exception e) {
            Sys.p(e.getMessage());
            return null;
        }
    }

    public static boolean setValueByKey(String key, String tenantId) {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(requestAttributes)) {
                return false;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            request.setAttribute(key, tenantId);
            return true;
        } catch (Exception e) {
            Sys.p(e.getMessage());
            return false;
        }
    }
}
