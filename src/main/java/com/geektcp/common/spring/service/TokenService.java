package com.geektcp.common.spring.service;

import com.geektcp.common.spring.model.vo.UserTokenVo;
import io.jsonwebtoken.Claims;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author geektcp on 2023/8/19 16:55.
 */
public interface TokenService extends Serializable {

    String getTokenIdFromToken(String token);

    String getTokenNameFromToken(String token);

    String getUsernameFromToken(String token);

    String getUserIdFromToken(String token);

    String getTenantIdFromToken(String token);

    String getClientIpFromToken(String token);

    String getValueFromToken(String token, String key);

    boolean expirationToken(String token);

    boolean expirationTokenByUsername(String username);

    Boolean invalid(String token);

    Boolean invalid(String token, String tokenTypeStr);

    Boolean isTokenExpired(String token);

    String generateToken(String tenantId, String username, String id, String type, String ip, String tokenTypeStr, String name, Long extendTime);

    String generateToken(UserTokenVo userTokenVo);

    String generateToken(UserTokenVo userTokenVo, long extendTime);

    Boolean canTokenBeRefreshed(String token, Date lastPasswordReset);

    String refreshToken(String token, String tokenTypeStr);

    Boolean validateToken(String token, String tokenTypeStr);

    Map<String, Object> validateTokenToRefresh(String token, String tokenTypeStr);

    Claims getClaimsFromToken(String token);

}
