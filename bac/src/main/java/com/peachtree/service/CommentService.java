package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peachtree.entity.Comment;
import com.peachtree.entity.User;
import com.peachtree.entity.CommunityPost;
import com.peachtree.mapper.CommentMapper;
import com.peachtree.mapper.UserMapper;
import com.peachtree.mapper.CommunityPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService extends ServiceImpl<CommentMapper, Comment> {
    
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final CommunityPostMapper communityPostMapper;
    
    /**
     * 添加评论
     */
    @Transactional
    public boolean addComment(Comment comment) {
        boolean success = commentMapper.insert(comment) > 0;
        if (success) {
            // 更新帖子评论数
            CommunityPost post = communityPostMapper.selectById(comment.getPostId());
            if (post != null) {
                post.setCommentCount(post.getCommentCount() + 1);
                communityPostMapper.updateById(post);
            }
        }
        return success;
    }
    
    /**
     * 获取帖子的所有评论
     */
    public List<Comment> getPostComments(Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(wrapper);
        
        // 填充用户信息和父评论信息
        comments.forEach(comment -> {
            // 填充评论者信息
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
                comment.setNickname(user.getNickname());
                comment.setAvatar(user.getAvatar());
            }
            
            // 填充父评论信息
            if (comment.getParentId() != null) {
                Comment parentComment = commentMapper.selectById(comment.getParentId());
                if (parentComment != null) {
                    User parentUser = userMapper.selectById(parentComment.getUserId());
                    if (parentUser != null) {
                        comment.setParentUsername(parentUser.getUsername());
                        comment.setParentNickname(parentUser.getNickname());
                    }
                }
            }
        });
        
        return comments;
    }
    
    /**
     * 删除评论
     */
    @Transactional
    public boolean deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            boolean success = commentMapper.deleteById(id) > 0;
            if (success) {
                // 更新帖子评论数
                CommunityPost post = communityPostMapper.selectById(comment.getPostId());
                if (post != null && post.getCommentCount() > 0) {
                    post.setCommentCount(post.getCommentCount() - 1);
                    communityPostMapper.updateById(post);
                }
            }
            return success;
        }
        return false;
    }
}
