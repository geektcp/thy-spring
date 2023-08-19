package com.geektcp.common.spring.service;

import com.geektcp.common.spring.model.vo.TokenVo;
import io.jsonwebtoken.Claims;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author geektcp on 2023/8/19 16:55.
 */
public interface TokenService extends Serializable {

    /**
     * get user info without token
     * @return TokenVo
     */
    ////////////////////////////////////////////////////////////
    TokenVo getTokenInfo();

    String getTokenId();

    String getTokenName();

    String getTokenType();

    String getUsername();

    String getUserId();

    String getTenantId();

    String getClientI();


    /**
     * get user info with token
     * @return TokenVo
     */
    ////////////////////////////////////////////////////////////
    TokenVo getTokenInfoFromToken(String token);

    String getTokenIdFromToken(String token);

    String getTokenNameFromToken(String token);

    String getTokenTypeFromToken(String token);

    String getUsernameFromToken(String token);

    String getUserIdFromToken(String token);

    String getTenantIdFromToken(String token);

    String getClientIpFromToken(String token);

    String getValueFromToken(String token, String key);


    ////////////////////////////////////////////////////////////
    boolean expirationToken(String token);

    boolean expirationTokenByUsername(String username);

    Boolean invalid(String token);

    Boolean invalid(String token, String tokenType);

    Boolean isTokenExpired(String token);

    String generateToken(String tenantId, String username, String id, String type, String ip, String tokenType, String name, Long extendTime);

    String generateToken(TokenVo tokenVo);

    String generateToken(TokenVo tokenVo, Long extendTime);

    Boolean canTokenBeRefreshed(String token, Date lastPasswordReset);

    String refreshToken(String token, String tokenType);

    Boolean validateToken(String token, String tokenType);

    Map<String, Object> validateTokenToRefresh(String token, String tokenType);

    Claims getClaimsFromToken(String token);

}
