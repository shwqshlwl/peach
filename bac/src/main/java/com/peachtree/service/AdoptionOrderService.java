package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peachtree.entity.AdoptionOrder;
import com.peachtree.entity.PeachTree;
import com.peachtree.mapper.AdoptionOrderMapper;
import com.peachtree.mapper.PeachTreeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionOrderService {
    
    private final AdoptionOrderMapper adoptionOrderMapper;
    private final PeachTreeMapper peachTreeMapper;
    
    @Transactional
    public AdoptionOrder createOrder(AdoptionOrder order) {
        // 检查桃树是否可认领
        PeachTree tree = peachTreeMapper.selectById(order.getTreeId());
        if (tree == null) {
            throw new RuntimeException("桃树不存在");
        }
        if (!"AVAILABLE".equals(tree.getStatus())) {
            throw new RuntimeException("该桃树已被认领或不可用");
        }
        
        // 生成订单号
        order.setOrderNo("PO" + System.currentTimeMillis());
        order.setPaymentStatus("PENDING");
        order.setStatus("ACTIVE");
        order.setStartDate(LocalDate.now());
        order.setEndDate(LocalDate.now().plusYears(1));
        
        // 设置金额（如果未设置，使用桃树价格）
        if (order.getAmount() == null) {
            order.setAmount(tree.getPricePerYear());
        }
        
        adoptionOrderMapper.insert(order);
        
        // 更新桃树状态为已认领
        tree.setStatus("ADOPTED");
        peachTreeMapper.updateById(tree);
        
        return order;
    }
    
    @Transactional
    public boolean payOrder(Long orderId, String paymentMethod) {
        AdoptionOrder order = adoptionOrderMapper.selectById(orderId);
        if (order != null && "PENDING".equals(order.getPaymentStatus())) {
            order.setPaymentStatus("PAID");
            order.setPaymentMethod(paymentMethod);
            order.setPaymentTime(LocalDateTime.now());
            return adoptionOrderMapper.updateById(order) > 0;
        }
        return false;
    }
    
    public List<AdoptionOrder> getUserOrders(Long userId) {
        LambdaQueryWrapper<AdoptionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionOrder::getUserId, userId)
               .orderByDesc(AdoptionOrder::getCreateTime);
        List<AdoptionOrder> orders = adoptionOrderMapper.selectList(wrapper);
        
        // 加载关联的桃树信息
        for (AdoptionOrder order : orders) {
            if (order.getTreeId() != null) {
                PeachTree tree = peachTreeMapper.selectById(order.getTreeId());
                order.setPeachTree(tree);
            }
        }
        
        return orders;
    }
    
    public AdoptionOrder getOrderDetail(Long orderId) {
        AdoptionOrder order = adoptionOrderMapper.selectById(orderId);
        if (order != null && order.getTreeId() != null) {
            PeachTree tree = peachTreeMapper.selectById(order.getTreeId());
            order.setPeachTree(tree);
        }
        return order;
    }
    
    public AdoptionOrder getOrderByNo(String orderNo) {
        LambdaQueryWrapper<AdoptionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionOrder::getOrderNo, orderNo);
        return adoptionOrderMapper.selectOne(wrapper);
    }
    
    public Page<AdoptionOrder> getAllOrders(int pageNum, int pageSize) {
        Page<AdoptionOrder> page = new Page<>(pageNum, pageSize);
        return adoptionOrderMapper.selectPage(page, null);
    }
}
