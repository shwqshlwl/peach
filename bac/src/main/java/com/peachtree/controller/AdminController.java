package com.peachtree.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peachtree.common.Result;
import com.peachtree.entity.PeachTree;
import com.peachtree.entity.User;
import com.peachtree.entity.CommunityPost;
import com.peachtree.service.PeachTreeService;
import com.peachtree.service.UserService;
import com.peachtree.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final PeachTreeService peachTreeService;
    private final UserService userService;
    private final CommunityService communityService;
    
    /**
     * 添加桃树
     */
    @PostMapping("/tree/add")
    public Result<String> addTree(@RequestBody PeachTree tree) {
        boolean success = peachTreeService.save(tree);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    /**
     * 更新桃树信息
     */
    @PutMapping("/tree/update")
    public Result<String> updateTree(@RequestBody PeachTree tree) {
        boolean success = peachTreeService.updateById(tree);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    /**
     * 删除桃树
     */
    @DeleteMapping("/tree/delete/{id}")
    public Result<String> deleteTree(@PathVariable Long id) {
        boolean success = peachTreeService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public Result<Page<User>> getUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return Result.success(userService.page(page));
    }
    
    /**
     * 禁用/启用用户
     */
    @PutMapping("/user/status/{id}")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(status);
            boolean success = userService.updateById(user);
            return success ? Result.success("操作成功") : Result.error("操作失败");
        }
        return Result.error("用户不存在");
    }
    
    /**
     * 删除社区帖子
     */
    @DeleteMapping("/post/delete/{id}")
    public Result<String> deletePost(@PathVariable Long id) {
        CommunityPost post = communityService.getPostDetail(id);
        if (post != null) {
            post.setStatus(0); // 隐藏帖子
            return Result.success("删除成功");
        }
        return Result.error("帖子不存在");
    }
    
    /**
     * 获取系统统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getStatistics() {
        // 这里可以添加统计逻辑
        return Result.success("统计功能待实现");
    }
}
