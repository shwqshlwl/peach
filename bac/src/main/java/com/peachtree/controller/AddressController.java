package com.peachtree.controller;

import com.peachtree.common.Result;
import com.peachtree.entity.ShippingAddress;
import com.peachtree.service.ShippingAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    
    private final ShippingAddressService shippingAddressService;
    
    @PostMapping("/add")
    public Result<String> addAddress(@RequestBody ShippingAddress address) {
        boolean success = shippingAddressService.addAddress(address);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<ShippingAddress>> getUserAddresses(@PathVariable Long userId) {
        return Result.success(shippingAddressService.getUserAddresses(userId));
    }
    
    @PutMapping("/update")
    public Result<String> updateAddress(@RequestBody ShippingAddress address) {
        boolean success = shippingAddressService.updateAddress(address);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteAddress(@PathVariable Long id) {
        boolean success = shippingAddressService.deleteAddress(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
