package com.peachtree.service;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.peachtree.config.WeChatPayConfig;
import com.peachtree.entity.AdoptionOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
public class WeChatPayService {
    
    private final WeChatPayConfig weChatPayConfig;
    private final JsapiService jsapiService;
    private final NativePayService nativePayService;
    private final Config config;
    
    @Autowired
    public WeChatPayService(WeChatPayConfig weChatPayConfig,
                           JsapiService jsapiService,
                           NativePayService nativePayService,
                           Config config) {
        this.weChatPayConfig = weChatPayConfig;
        this.jsapiService = jsapiService;
        this.nativePayService = nativePayService;
        this.config = config;
        log.info("微信支付服务已启用");
    }
    
    /**
     * 创建JSAPI支付订单（公众号/小程序支付）
     * @param order 认领订单
     * @param openid 用户openid
     * @return 预支付交易会话标识
     */
    public Map<String, String> createJsapiOrder(AdoptionOrder order, String openid) {
        try {
            // 构建请求参数
            com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request = 
                new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
            
            // 应用ID
            request.setAppid(weChatPayConfig.getAppId());
            // 商户号
            request.setMchid(weChatPayConfig.getMchId());
            // 商品描述
            request.setDescription("桃树认领-" + order.getOrderNo());
            // 商户订单号
            request.setOutTradeNo(order.getOrderNo());
            // 通知地址
            request.setNotifyUrl(weChatPayConfig.getNotifyUrl());
            
            // 订单金额
            Amount amount = new Amount();
            // 金额单位为分
            amount.setTotal(order.getAmount().multiply(new BigDecimal("100")).intValue());
            amount.setCurrency("CNY");
            request.setAmount(amount);
            
            // 支付者信息
            Payer payer = new Payer();
            payer.setOpenid(openid);
            request.setPayer(payer);
            
            // 调用下单方法，得到应答
            com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse response = jsapiService.prepay(request);
            
            // 构建返回结果
            Map<String, String> result = new HashMap<>();
            result.put("prepayId", response.getPrepayId());
            
            log.info("微信JSAPI支付订单创建成功，订单号：{}", order.getOrderNo());
            return result;
            
        } catch (ServiceException e) {
            log.error("微信支付下单失败，订单号：{}，错误信息：{}", order.getOrderNo(), e.getMessage());
            throw new RuntimeException("微信支付下单失败：" + e.getErrorMessage());
        }
    }
    
    /**
     * 创建Native支付订单（扫码支付）
     * @param order 认领订单
     * @return 二维码链接
     */
    public String createNativeOrder(AdoptionOrder order) {
        try {
            // 构建请求参数
            PrepayRequest request = new PrepayRequest();
            
            // 应用ID
            request.setAppid(weChatPayConfig.getAppId());
            // 商户号
            request.setMchid(weChatPayConfig.getMchId());
            // 商品描述
            request.setDescription("桃树认领-" + order.getOrderNo());
            // 商户订单号
            request.setOutTradeNo(order.getOrderNo());
            // 通知地址
            request.setNotifyUrl(weChatPayConfig.getNotifyUrl());
            
            // 订单金额
            com.wechat.pay.java.service.payments.nativepay.model.Amount amount = 
                new com.wechat.pay.java.service.payments.nativepay.model.Amount();
            // 金额单位为分
            amount.setTotal(order.getAmount().multiply(new BigDecimal("100")).intValue());
            amount.setCurrency("CNY");
            request.setAmount(amount);
            
            // 调用下单方法，得到应答
            PrepayResponse response = nativePayService.prepay(request);
            
            log.info("微信Native支付订单创建成功，订单号：{}，二维码链接：{}", 
                    order.getOrderNo(), response.getCodeUrl());
            
            return response.getCodeUrl();
            
        } catch (ServiceException e) {
            log.error("微信扫码支付下单失败，订单号：{}，错误信息：{}", order.getOrderNo(), e.getMessage());
            throw new RuntimeException("微信扫码支付下单失败：" + e.getErrorMessage());
        }
    }
    
    /**
     * 查询订单
     * @param outTradeNo 商户订单号
     * @return 订单信息
     */
    public Transaction queryOrder(String outTradeNo) {
        try {
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(weChatPayConfig.getMchId());
            request.setOutTradeNo(outTradeNo);
            
            return jsapiService.queryOrderByOutTradeNo(request);
        } catch (ServiceException e) {
            log.error("查询订单失败，订单号：{}，错误信息：{}", outTradeNo, e.getMessage());
            throw new RuntimeException("查询订单失败：" + e.getErrorMessage());
        }
    }
    
    /**
     * 关闭订单
     * @param outTradeNo 商户订单号
     */
    public void closeOrder(String outTradeNo) {
        try {
            CloseOrderRequest request = new CloseOrderRequest();
            request.setMchid(weChatPayConfig.getMchId());
            request.setOutTradeNo(outTradeNo);
            
            jsapiService.closeOrder(request);
            log.info("订单关闭成功，订单号：{}", outTradeNo);
        } catch (ServiceException e) {
            log.error("关闭订单失败，订单号：{}，错误信息：{}", outTradeNo, e.getMessage());
            throw new RuntimeException("关闭订单失败：" + e.getErrorMessage());
        }
    }
}
