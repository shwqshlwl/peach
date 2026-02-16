package com.peachtree.controller;

import com.peachtree.common.Result;
import com.peachtree.entity.Comment;
import com.peachtree.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    /**
     * 添加评论
     */
    @PostMapping("/add")
    public Result<String> addComment(@RequestBody Comment comment) {
        boolean success = commentService.addComment(comment);
        return success ? Result.success("评论成功") : Result.error("评论失败");
    }
    
    /**
     * 获取帖子的所有评论
     */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getPostComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getPostComments(postId);
        return Result.success(comments);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteComment(@PathVariable Long id) {
        boolean success = commentService.deleteComment(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
