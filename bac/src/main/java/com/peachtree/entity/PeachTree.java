package com.peachtree.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("peach_tree")
public class PeachTree {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String treeNo;
    private String treeName;
    private String variety;
    private Integer treeAge;
    private BigDecimal estimatedYield;
    private BigDecimal locationX;
    private BigDecimal locationY;
    private String status;
    private BigDecimal pricePerYear;
    private String description;
    private String images;
    private String panoramaUrl;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
