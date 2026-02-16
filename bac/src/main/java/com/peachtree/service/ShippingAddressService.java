package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peachtree.entity.ShippingAddress;
import com.peachtree.mapper.ShippingAddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {
    
    private final ShippingAddressMapper shippingAddressMapper;
    
    @Transactional
    public boolean addAddress(ShippingAddress address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            LambdaQueryWrapper<ShippingAddress> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShippingAddress::getUserId, address.getUserId())
                   .eq(ShippingAddress::getIsDefault, 1);
            List<ShippingAddress> defaultAddresses = shippingAddressMapper.selectList(wrapper);
            for (ShippingAddress addr : defaultAddresses) {
                addr.setIsDefault(0);
                shippingAddressMapper.updateById(addr);
            }
        }
        return shippingAddressMapper.insert(address) > 0;
    }
    
    public List<ShippingAddress> getUserAddresses(Long userId) {
        LambdaQueryWrapper<ShippingAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShippingAddress::getUserId, userId)
               .orderByDesc(ShippingAddress::getIsDefault)
               .orderByDesc(ShippingAddress::getCreateTime);
        return shippingAddressMapper.selectList(wrapper);
    }
    
    public boolean updateAddress(ShippingAddress address) {
        return shippingAddressMapper.updateById(address) > 0;
    }
    
    public boolean deleteAddress(Long id) {
        return shippingAddressMapper.deleteById(id) > 0;
    }
}
