package com.peachtree.controller;

import com.peachtree.common.Result;
import com.peachtree.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interaction")
@RequiredArgsConstructor
public class InteractionController {
    
    private final InteractionService interactionService;
    
    @PostMapping("/action")
    public Result<String> performAction(
            @RequestParam Long userId,
            @RequestParam Long treeId,
            @RequestParam String actionType) {
        boolean success = interactionService.performAction(userId, treeId, actionType);
        return success ? Result.success("操作成功，积分已增加") : Result.error("操作失败");
    }
}
