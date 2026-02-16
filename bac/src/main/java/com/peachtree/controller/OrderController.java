package com.peachtree.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peachtree.common.Result;
import com.peachtree.entity.AdoptionOrder;
import com.peachtree.service.AdoptionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final AdoptionOrderService adoptionOrderService;
    
    @PostMapping("/create")
    public Result<AdoptionOrder> createOrder(@RequestBody AdoptionOrder order) {
        try {
            AdoptionOrder createdOrder = adoptionOrderService.createOrder(order);
            return Result.success(createdOrder);
        } catch (Exception e) {
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/pay/{orderId}")
    public Result<String> payOrder(@PathVariable Long orderId, @RequestParam String paymentMethod) {
        boolean success = adoptionOrderService.payOrder(orderId, paymentMethod);
        return success ? Result.success("支付成功") : Result.error("支付失败");
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<AdoptionOrder>> getUserOrders(@PathVariable Long userId) {
        return Result.success(adoptionOrderService.getUserOrders(userId));
    }
    
    @GetMapping("/detail/{orderId}")
    public Result<AdoptionOrder> getOrderDetail(@PathVariable Long orderId) {
        AdoptionOrder order = adoptionOrderService.getOrderDetail(orderId);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }
    
    @GetMapping("/all")
    public Result<Page<AdoptionOrder>> getAllOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(adoptionOrderService.getAllOrders(pageNum, pageSize));
    }
}
