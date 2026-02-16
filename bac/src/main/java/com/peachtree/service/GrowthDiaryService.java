package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peachtree.entity.GrowthDiary;
import com.peachtree.mapper.GrowthDiaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GrowthDiaryService {
    
    private final GrowthDiaryMapper growthDiaryMapper;
    
    public boolean addDiary(GrowthDiary diary) {
        return growthDiaryMapper.insert(diary) > 0;
    }
    
    public List<GrowthDiary> getTreeDiaries(Long treeId) {
        LambdaQueryWrapper<GrowthDiary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrowthDiary::getTreeId, treeId)
               .orderByDesc(GrowthDiary::getRecordDate);
        return growthDiaryMapper.selectList(wrapper);
    }
    
    public boolean updateDiary(GrowthDiary diary) {
        return growthDiaryMapper.updateById(diary) > 0;
    }
    
    public boolean deleteDiary(Long id) {
        return growthDiaryMapper.deleteById(id) > 0;
    }
}
