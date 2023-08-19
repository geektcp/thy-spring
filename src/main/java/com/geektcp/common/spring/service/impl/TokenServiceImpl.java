package com.geektcp.common.spring.service.impl;

import com.geektcp.common.mosheh.constant.CommonStatus;
import com.geektcp.common.mosheh.exception.BaseException;
import com.geektcp.common.mosheh.generator.IdGenerator;
import com.geektcp.common.mosheh.util.DateUtils;
import com.geektcp.common.spring.model.vo.TokenVo;
import com.geektcp.common.spring.service.TokenService;
import com.geektcp.common.spring.util.HttpRequestHeadUtils;
import com.geektcp.common.spring.util.IPUtils;
import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author geektcp on 2023/8/19 16:56.
 */

@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

    private static final long serialVersionUID = -3301605591108950415L;

    public static final String CLAIM_KEY_ID = "id";             // token id
    public static final String CLAIM_KEY_NAME = "name";         // token name
    public static final String CLAIM_KEY_TYPE = "type";         // token type

    public static final String CLAIM_KEY_USERNAME = "sub";      // user name
    public static final String CLAIM_KEY_UID = "uid";           // user id
    public static final String CLAIM_KEY_TID = "tid";           // tenant id
    public static final String CLAIM_KEY_IP = "ip";             // client ip
    public static final String CLAIM_KEY_CREATED = "created";
    public static final String CLAIM_KEY_USER_TYPE = "user_type";
    public static final String CLAIM_KEY_OAUTH_TYPE = "oauth_type";

    @Value("${gate.jwt.secret:UNKNOWN}")
    private String secret;

    @Value("${gate.jwt.expiration:7200}")
    private Long expiration;

    private static final long EXTEND_TIME = 60 * 60;  // expired 30 min

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public TokenVo getTokenInfoFromToken(String token) {
        return new TokenVo(
                getValueFromToken(token, CLAIM_KEY_ID),
                getValueFromToken(token, CLAIM_KEY_NAME),
                getValueFromToken(token, CLAIM_KEY_TYPE),
                getValueFromToken(token, CLAIM_KEY_UID),
                getValueFromToken(token, CLAIM_KEY_USERNAME),
                getValueFromToken(token, CLAIM_KEY_USER_TYPE),
                getValueFromToken(token, CLAIM_KEY_TID),
                getValueFromToken(token, CLAIM_KEY_IP)
        );
    }

    @Override
    public String getTokenIdFromToken(String token) {
        return getValueFromToken(token, CLAIM_KEY_ID);
    }

    @Override
    public String getTokenNameFromToken(String token) {
        return getValueFromToken(token, CLAIM_KEY_NAME);
    }

    @Override
    public String getTokenTypeFromToken(String token) {
        return getValueFromToken(token, CLAIM_KEY_TYPE);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getValueFromToken(token, CLAIM_KEY_USERNAME);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return getValueFromToken(token, CLAIM_KEY_UID);
    }

    @Override
    public String getTenantIdFromToken(String token) {
        return getValueFromToken(token, CLAIM_KEY_TID);
    }

    @Override
    public String getClientIpFromToken(String token) {
        return getValueFromToken(token, CLAIM_KEY_IP);
    }

    /**
     * @param token token
     * @param key   must be the specified key which def with static final
     * @return result
     */
    @Override
    public String getValueFromToken(String token, String key) {
        String Value;
        try {
            final Claims claims = getClaimsFromToken(token);
            Value = (String) claims.get(key);
        } catch (Exception e) {
            Value = null;
        }
        return Value;
    }

    @Override
    public boolean expirationToken(String token) {
        String username = getUsernameFromToken(token);
        return expirationTokenByUsername(username);
    }

    /**
     * delete all of redis cache
     *
     * @param username username
     * @return result
     */
    @Override
    public boolean expirationTokenByUsername(String username) {
        Set<String> keys = redisTemplate.keys("*" + username + "*");
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                String token = (String) redisTemplate.opsForValue().get(key);
                String tokenID = this.getValueFromToken(token, CLAIM_KEY_ID);
                if (StringUtils.contains(key, username + ":" + tokenID)) {
                    redisTemplate.delete(key);
                }
            }
        }
        return true;
    }

    /**
     * Accuracy delete part of redis cache
     *
     * @param token token
     * @return result
     */
    @Override
    public Boolean invalid(String token) {
        try {
            String username = this.getUsernameFromToken(token);
            String tokenID = this.getValueFromToken(token, CLAIM_KEY_ID);
            List<String> keys = new ArrayList<>();
            String key = getKey(IPUtils.getIp(), username, tokenID, null);
            keys.add(key);
            redisTemplate.delete(keys);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Accuracy delete part of redis cache
     * Usually delete one key
     *
     * @param token     token
     * @param tokenType token type string
     * @return result
     */
    @Override
    public Boolean invalid(String token, String tokenType) {
        try {
            String username = this.getUsernameFromToken(token);
            String tokenID = this.getValueFromToken(token, CLAIM_KEY_ID);
            List<String> keys = new ArrayList<>();
            String key = getKey(IPUtils.getIp(), username, tokenID, tokenType);
            keys.add(key);
            redisTemplate.delete(keys);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    @Override
    public String generateToken(String userId,
                                String username,
                                String userType,
                                String tenantId,
                                String clientIp,
                                String tokenType,
                                String tokenName,
                                Long extendTime) {
        if (StringUtils.isEmpty(userId)) {
            throw new BaseException(CommonStatus.JWT_BASIC_INVALID);
        }
        if (StringUtils.isNoneEmpty(tenantId)) {
            HttpRequestHeadUtils.setTenantId(tenantId);
        }

        String tokenId = IdGenerator.getId(CLAIM_KEY_ID);
        if (StringUtils.isEmpty(clientIp)) {
            clientIp = IPUtils.getIp();
        }
        String strKey = getKey(clientIp, username, tokenId, tokenType);
        Map<String, Object> claims = Maps.newHashMap();
        claims.put(CLAIM_KEY_ID, tokenId);
        claims.put(CLAIM_KEY_NAME, tokenName);
        claims.put(CLAIM_KEY_TYPE, tokenType);

        claims.put(CLAIM_KEY_UID, userId);
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_USER_TYPE, userType);
        claims.put(CLAIM_KEY_TID, tenantId);
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put(CLAIM_KEY_IP, clientIp);

        String token = generateTokenByClaims(claims, extendTime);
        long t3 = System.currentTimeMillis();

        if (Objects.isNull(extendTime)) {
            redisTemplate.opsForValue().set(strKey, token, expiration, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(strKey, token, extendTime, TimeUnit.SECONDS);
        }
        return token;
    }

    @Override
    public String generateToken(TokenVo tokenVo) {
        return generateToken(tokenVo, null);
    }

    @Override
    public String generateToken(TokenVo tokenVo, Long extendTime) {
        return generateToken(
                tokenVo.getUserId(),
                tokenVo.getUsername(),
                tokenVo.getUserType(),
                tokenVo.getTenantId(),
                tokenVo.getClientIp(),
                tokenVo.getTokenType(),
                tokenVo.getTokenName(),
                extendTime);
    }

    @Override
    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && !isTokenExpired(token);
    }

    @Override
    public String refreshToken(String token, String tokenType) {
        String userName = getUsernameFromToken(token);
        String tokenID = getValueFromToken(token, CLAIM_KEY_ID);
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(tokenID)) {
            return null;
        }
        String ip = IPUtils.getIp();
        String strKey = getKey(ip, userName, tokenID, tokenType);
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateTokenByClaims(claims);
            redisTemplate.opsForValue().set(strKey, refreshedToken, expiration, TimeUnit.SECONDS);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    @Override
    public Boolean validateToken(String token, String tokenType) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        final String tokenID = getValueFromToken(token, CLAIM_KEY_ID);
        final String username = getUsernameFromToken(token);
        if (username.isEmpty() || tokenID.isEmpty()) {
            return false;
        }
        if (isTokenExpired(token)) {
            return false;
        }
        String oauthType = getValueFromToken(token, CLAIM_KEY_OAUTH_TYPE);
        if (!StringUtils.isEmpty(oauthType)) {
            return true;
        }

        String key = getKey(IPUtils.getIp(), username, tokenID, tokenType);
        Object existToken = redisTemplate.opsForValue().get(key);
        return (token.equals(existToken));
    }

    @Override
    public Map<String, Object> validateTokenToRefresh(String token, String tokenType) {
        Map<String, Object> result = new HashMap<>();
        boolean flag = validateToken(token, tokenType);
        result.put("flag", flag);
        if (!flag) {
            return result;
        }
        final Date expirationDate = getExpirationDateFromToken(token);
        Date checkDate = DateUtils.getPreMin(new Date(), 30);
        if (expirationDate.before(checkDate)) {
            final String username = getUsernameFromToken(token);
            final String tokenID = getValueFromToken(token, CLAIM_KEY_ID);
            String key = getKey(IPUtils.getIp(), username, tokenID, tokenType);
            TokenVo tokenVo = new TokenVo(
                    getValueFromToken(token, CLAIM_KEY_ID),
                    getValueFromToken(token, CLAIM_KEY_NAME),
                    tokenType,
                    getValueFromToken(token, CLAIM_KEY_UID),
                    username,
                    getValueFromToken(token, CLAIM_KEY_USER_TYPE),
                    getValueFromToken(token, CLAIM_KEY_TID),
                    IPUtils.getIp()

            );
            String newToken = generateToken(tokenVo, EXTEND_TIME);
            if (StringUtils.isBlank(newToken)) {
                log.error("generate token failed!");
                result.put("flag", false);
                return result;
            }
            result.put("newToken", newToken);
            redisTemplate.delete(key);
        }
        return result;
    }

    @Override
    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }


    ////////////////////////////////////////////////////////////////////
    private String generateTokenByClaims(Map<String, Object> claims) {
        return generateTokenByClaims(claims, null);
    }

    private String generateTokenByClaims(Map<String, Object> claims, Long extendTime) {
        return Jwts.builder().setClaims(claims)
                .setExpiration(generateExpirationDate(extendTime))
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();
    }

    private Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    private Date getExpirationDateFromToken(String token) {
        try {
            final Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("exception", e);
            throw new BaseException(CommonStatus.JWT_TOKEN_EXPIRED);
        }
    }

    private Date generateExpirationDate() {
        return generateExpirationDate(null);
    }

    private Date generateExpirationDate(Long extendTime) {
        long current = System.currentTimeMillis();
        if (extendTime != null) {
            current += (extendTime * 1000);
        } else {
            current += (expiration * 1000);
        }
        return new Date(current);
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private String getKey(String ip, String username, String tokenId, String tokenType) {
        return tokenType + ":" + ip + ":" + username + ":" + tokenId;
    }
}

