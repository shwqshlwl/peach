package com.peachtree.controller;

import com.peachtree.common.Result;
import com.peachtree.entity.User;
import com.peachtree.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            registeredUser.setPassword(null);
            return Result.success(registeredUser);
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户名或密码错误");
    }
    
    @GetMapping("/user/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }
}
