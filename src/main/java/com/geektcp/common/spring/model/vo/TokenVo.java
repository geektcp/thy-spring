package com.geektcp.common.spring.model.vo;

import com.geektcp.common.spring.constant.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanghaiyang on 2021/1/21 13:41.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVo {

    String tokeId;          // token id
    String tokenName;       // token name
    String tokenType;       // token type: SYS|AUTH|anything

    String userId;          // user id
    String username;        // user name
    String userType;        // user type
    String tenantId;        // tenant id
    String clientIp;        // client ip

}
