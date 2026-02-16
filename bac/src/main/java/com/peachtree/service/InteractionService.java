package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peachtree.entity.InteractionRecord;
import com.peachtree.mapper.InteractionRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InteractionService {
    
    private final InteractionRecordMapper interactionRecordMapper;
    private final UserService userService;
    
    @Transactional
    public boolean performAction(Long userId, Long treeId, String actionType) {
        // 检查今天是否已经执行过相同操作（防止重复刷积分）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
        
        LambdaQueryWrapper<InteractionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InteractionRecord::getUserId, userId)
               .eq(InteractionRecord::getTreeId, treeId)
               .eq(InteractionRecord::getActionType, actionType)
               .ge(InteractionRecord::getCreateTime, todayStart)
               .lt(InteractionRecord::getCreateTime, todayEnd);
        
        Long count = interactionRecordMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("今天已经执行过该操作了");
        }
        
        InteractionRecord record = new InteractionRecord();
        record.setUserId(userId);
        record.setTreeId(treeId);
        record.setActionType(actionType);
        
        // 根据操作类型给予积分
        int points = switch (actionType) {
            case "WATER" -> 5;
            case "FERTILIZE" -> 10;
            case "SIGN_IN" -> 3;
            default -> 0;
        };
        
        record.setPointsEarned(points);
        
        // 保存互动记录
        boolean saved = interactionRecordMapper.insert(record) > 0;
        
        // 增加用户积分
        if (saved && points > 0) {
            userService.addPoints(userId, points);
        }
        
        return saved;
    }
}
