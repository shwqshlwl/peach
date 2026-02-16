package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peachtree.entity.User;
import com.peachtree.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.getOne(wrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setMemberLevel(1);
        user.setPoints(0);
        user.setStatus(1);
        this.save(user);
        return user;
    }
    
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    public User getUserById(Long id) {
        return this.getById(id);
    }
    
    public boolean updateUser(User user) {
        return this.updateById(user);
    }
    
    @Transactional
    public boolean addPoints(Long userId, Integer points) {
        User user = this.getById(userId);
        if (user != null) {
            Integer currentPoints = user.getPoints() != null ? user.getPoints() : 0;
            user.setPoints(currentPoints + points);
            return this.updateById(user);
        }
        return false;
    }
}
