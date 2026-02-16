package com.peachtree.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("growth_diary")
public class GrowthDiary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long treeId;
    private String title;
    private String content;
    private String images;
    private String growthStage;
    private LocalDate recordDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
