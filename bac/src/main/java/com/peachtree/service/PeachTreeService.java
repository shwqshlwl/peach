package com.peachtree.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peachtree.entity.PeachTree;
import com.peachtree.mapper.PeachTreeMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PeachTreeService extends ServiceImpl<PeachTreeMapper, PeachTree> {
    
    public Page<PeachTree> getAvailableTrees(int pageNum, int pageSize, String status) {
        Page<PeachTree> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PeachTree> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PeachTree::getStatus, status);
        }
        return this.page(page, wrapper);
    }
    
    public PeachTree getTreeDetail(Long id) {
        return this.getById(id);
    }
    
    public boolean saveTree(PeachTree tree) {
        return this.saveOrUpdate(tree);
    }
    
    public boolean deleteTree(Long id) {
        return this.removeById(id);
    }
    
    public List<PeachTree> getAllTrees() {
        return this.list();
    }
}
