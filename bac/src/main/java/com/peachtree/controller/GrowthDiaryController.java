package com.peachtree.controller;

import com.peachtree.common.Result;
import com.peachtree.entity.GrowthDiary;
import com.peachtree.service.GrowthDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/growth-diary")
@RequiredArgsConstructor
public class GrowthDiaryController {
    
    private final GrowthDiaryService growthDiaryService;
    
    @PostMapping("/add")
    public Result<String> addDiary(@RequestBody GrowthDiary diary) {
        boolean success = growthDiaryService.addDiary(diary);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @GetMapping("/tree/{treeId}")
    public Result<List<GrowthDiary>> getTreeDiaries(@PathVariable Long treeId) {
        return Result.success(growthDiaryService.getTreeDiaries(treeId));
    }
    
    @PutMapping("/update")
    public Result<String> updateDiary(@RequestBody GrowthDiary diary) {
        boolean success = growthDiaryService.updateDiary(diary);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteDiary(@PathVariable Long id) {
        boolean success = growthDiaryService.deleteDiary(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
