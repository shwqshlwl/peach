package com.peachtree.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peachtree.common.Result;
import com.peachtree.entity.PeachTree;
import com.peachtree.service.PeachTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/peach-trees")
@RequiredArgsConstructor
public class PeachTreeController {
    
    private final PeachTreeService peachTreeService;
    
    @GetMapping("/list")
    public Result<Page<PeachTree>> getTreeList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        Page<PeachTree> page = peachTreeService.getAvailableTrees(pageNum, pageSize, status);
        return Result.success(page);
    }
    
    @GetMapping("/detail/{id}")
    public Result<PeachTree> getTreeDetail(@PathVariable Long id) {
        PeachTree tree = peachTreeService.getTreeDetail(id);
        if (tree != null) {
            return Result.success(tree);
        }
        return Result.error("桃树不存在");
    }
    
    @PostMapping("/save")
    public Result<String> saveTree(@RequestBody PeachTree tree) {
        boolean success = peachTreeService.saveTree(tree);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteTree(@PathVariable Long id) {
        boolean success = peachTreeService.deleteTree(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @GetMapping("/all")
    public Result<List<PeachTree>> getAllTrees() {
        return Result.success(peachTreeService.getAllTrees());
    }
}
