package com.peachtree.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peachtree.common.Result;
import com.peachtree.entity.CommunityPost;
import com.peachtree.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;
    
    @PostMapping("/post")
    public Result<String> createPost(@RequestBody CommunityPost post) {
        boolean success = communityService.createPost(post);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }
    
    @GetMapping("/posts")
    public Result<Page<CommunityPost>> getPosts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(communityService.getPosts(pageNum, pageSize));
    }
    
    @GetMapping("/post/{id}")
    public Result<CommunityPost> getPostDetail(@PathVariable Long id) {
        CommunityPost post = communityService.getPostDetail(id);
        return post != null ? Result.success(post) : Result.error("帖子不存在");
    }
    
    @PostMapping("/like/{postId}")
    public Result<Map<String, Object>> likePost(
            @PathVariable Long postId, 
            @RequestParam Long userId) {
        boolean liked = communityService.likePost(postId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("message", liked ? "点赞成功" : "取消点赞");
        return Result.success(result);
    }
    
    @GetMapping("/like/status")
    public Result<Boolean> getLikeStatus(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        boolean hasLiked = communityService.hasLiked(postId, userId);
        return Result.success(hasLiked);
    }
}
