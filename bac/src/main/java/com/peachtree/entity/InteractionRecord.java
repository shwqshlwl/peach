package com.peachtree.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("interaction_record")
public class InteractionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long treeId;
    private String actionType;
    private Integer pointsEarned;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
