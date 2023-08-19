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
public class UserTokenVo {

    String id;              // token id
    String name;            // token name
    String tokenType;      // token type: SYS|AUTH|null

    String username;        // user id
    String userId;          // user name
    String tenantId;        // tenant id
    String type;            // user type
    String ip;              // client ip

}
