package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peachtree.entity.CommunityPost;
import com.peachtree.entity.User;
import com.peachtree.entity.PostLike;
import com.peachtree.mapper.CommunityPostMapper;
import com.peachtree.mapper.UserMapper;
import com.peachtree.mapper.PostLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {
    
    private final CommunityPostMapper communityPostMapper;
    private final UserMapper userMapper;
    private final PostLikeMapper postLikeMapper;
    
    public boolean createPost(CommunityPost post) {
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        return communityPostMapper.insert(post) > 0;
    }
    
    public Page<CommunityPost> getPosts(int pageNum, int pageSize) {
        Page<CommunityPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityPost::getStatus, 1)
               .orderByDesc(CommunityPost::getCreateTime);
        Page<CommunityPost> result = communityPostMapper.selectPage(page, wrapper);
        
        // 填充用户信息
        result.getRecords().forEach(post -> {
            User user = userMapper.selectById(post.getUserId());
            if (user != null) {
                post.setUsername(user.getUsername());
                post.setNickname(user.getNickname());
                post.setAvatar(user.getAvatar());
            }
        });
        
        return result;
    }
    
    public CommunityPost getPostDetail(Long id) {
        CommunityPost post = communityPostMapper.selectById(id);
        if (post != null) {
            User user = userMapper.selectById(post.getUserId());
            if (user != null) {
                post.setUsername(user.getUsername());
                post.setNickname(user.getNickname());
                post.setAvatar(user.getAvatar());
            }
        }
        return post;
    }
    
    /**
     * 点赞帖子（一人只能点赞一次）
     */
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        // 检查是否已点赞
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
               .eq(PostLike::getUserId, userId);
        PostLike existingLike = postLikeMapper.selectOne(wrapper);
        
        if (existingLike != null) {
            // 已点赞，取消点赞
            postLikeMapper.deleteById(existingLike.getId());
            CommunityPost post = communityPostMapper.selectById(postId);
            if (post != null && post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
                communityPostMapper.updateById(post);
            }
            return false; // 返回false表示取消点赞
        } else {
            // 未点赞，添加点赞
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            postLikeMapper.insert(like);
            
            CommunityPost post = communityPostMapper.selectById(postId);
            if (post != null) {
                post.setLikeCount(post.getLikeCount() + 1);
                communityPostMapper.updateById(post);
            }
            return true; // 返回true表示点赞成功
        }
    }
    
    /**
     * 检查用户是否已点赞
     */
    public boolean hasLiked(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
               .eq(PostLike::getUserId, userId);
        return postLikeMapper.selectCount(wrapper) > 0;
    }
}
