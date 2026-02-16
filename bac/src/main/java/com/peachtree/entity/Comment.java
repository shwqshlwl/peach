package com.peachtree.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private String images;  // 图片URLs（JSON数组）
    private Long parentId;  // 父评论ID
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 关联用户信息（不映射到数据库）
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String nickname;
    
    @TableField(exist = false)
    private String avatar;
    
    // 父评论信息（不映射到数据库）
    @TableField(exist = false)
    private String parentUsername;
    
    @TableField(exist = false)
    private String parentNickname;
}
