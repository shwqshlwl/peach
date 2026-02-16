package com.peachtree.controller;

import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.peachtree.common.Result;
import com.peachtree.entity.AdoptionOrder;
import com.peachtree.service.AdoptionOrderService;
import com.peachtree.service.WeChatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment/wechat")
@ConditionalOnBean(WeChatPayService.class)
public class WeChatPayController {
    
    private final WeChatPayService weChatPayService;
    private final AdoptionOrderService adoptionOrderService;
    private final NotificationParser notificationParser;
    
    @Autowired
    public WeChatPayController(WeChatPayService weChatPayService,
                              AdoptionOrderService adoptionOrderService,
                              NotificationParser notificationParser) {
        this.weChatPayService = weChatPayService;
        this.adoptionOrderService = adoptionOrderService;
        this.notificationParser = notificationParser;
    }
    
    /**
     * 创建JSAPI支付订单（公众号/小程序支付）
     */
    @PostMapping("/jsapi/create")
    public Result<Map<String, String>> createJsapiOrder(
            @RequestParam Long orderId,
            @RequestParam String openid) {
        try {
            AdoptionOrder order = adoptionOrderService.getOrderDetail(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            if (!"PENDING".equals(order.getPaymentStatus())) {
                return Result.error("订单状态异常");
            }
            
            Map<String, String> response = weChatPayService.createJsapiOrder(order, openid);
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建JSAPI支付订单失败", e);
            return Result.error("创建支付订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建Native支付订单（扫码支付）
     */
    @PostMapping("/native/create")
    public Result<Map<String, String>> createNativeOrder(@RequestParam Long orderId) {
        try {
            AdoptionOrder order = adoptionOrderService.getOrderDetail(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            if (!"PENDING".equals(order.getPaymentStatus())) {
                return Result.error("订单状态异常");
            }
            
            String codeUrl = weChatPayService.createNativeOrder(order);
            
            Map<String, String> result = new HashMap<>();
            result.put("codeUrl", codeUrl);
            result.put("orderNo", order.getOrderNo());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("创建Native支付订单失败", e);
            return Result.error("创建支付订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询订单支付状态
     */
    @GetMapping("/query/{orderNo}")
    public Result<Transaction> queryOrder(@PathVariable String orderNo) {
        try {
            Transaction transaction = weChatPayService.queryOrder(orderNo);
            return Result.success(transaction);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return Result.error("查询订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 微信支付回调通知
     */
    @PostMapping("/notify")
    public Map<String, String> payNotify(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // 构建请求参数
            com.wechat.pay.java.core.notification.RequestParam requestParam = 
                new com.wechat.pay.java.core.notification.RequestParam.Builder()
                    .serialNumber(request.getHeader("Wechatpay-Serial"))
                    .nonce(request.getHeader("Wechatpay-Nonce"))
                    .signature(request.getHeader("Wechatpay-Signature"))
                    .timestamp(request.getHeader("Wechatpay-Timestamp"))
                    .body(request.getReader().lines().reduce("", (a, b) -> a + b))
                    .build();
            
            // 解析通知
            Transaction transaction = notificationParser.parse(requestParam, Transaction.class);
            
            log.info("收到微信支付回调通知，订单号：{}，交易状态：{}", 
                    transaction.getOutTradeNo(), transaction.getTradeState());
            
            // 处理支付成功
            if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                // 更新订单状态
                AdoptionOrder order = adoptionOrderService.getOrderByNo(transaction.getOutTradeNo());
                if (order != null && "PENDING".equals(order.getPaymentStatus())) {
                    adoptionOrderService.payOrder(order.getId(), "WECHAT");
                    log.info("订单支付成功，订单号：{}", transaction.getOutTradeNo());
                }
            }
            
            // 返回成功响应
            response.put("code", "SUCCESS");
            response.put("message", "成功");
            
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            response.put("code", "FAIL");
            response.put("message", "失败");
        }
        
        return response;
    }
    
    /**
     * 关闭订单
     */
    @PostMapping("/close/{orderNo}")
    public Result<String> closeOrder(@PathVariable String orderNo) {
        try {
            weChatPayService.closeOrder(orderNo);
            return Result.success("订单已关闭");
        } catch (Exception e) {
            log.error("关闭订单失败", e);
            return Result.error("关闭订单失败：" + e.getMessage());
        }
    }
}
