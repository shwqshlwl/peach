package com.peachtree.dto;

import lombok.Data;

@Data
public class WeChatPayRequest {
    private Long orderId;
    private String openid;  // 用户openid（JSAPI支付需要）
    private String payType; // 支付类型：JSAPI-公众号/小程序支付，NATIVE-扫码支付
}
