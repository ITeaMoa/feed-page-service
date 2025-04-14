package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ReplyEntity;
import com.example.demo.service.ReplyService;

@RestController
@RequestMapping("/feed")
public class ReplyController {
    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @PostMapping("/{feedId}/comments/{commentId}/replies")
    public ResponseEntity<String> addReply(
    @PathVariable("feedId") String feedId,
    @PathVariable("commentId") String commentId,
    @RequestParam("userId") String userId,
    @RequestBody Map<String, String> request
) {
    String content = request.get("content");
    replyService.addReply(feedId, commentId, userId, content);
    return ResponseEntity.ok("대댓글이 추가되었습니다.");
}


    @GetMapping("/{feedId}/comments/{commentId}/replies")
    public ResponseEntity<List<ReplyEntity>> getReplies(
            @PathVariable("feedId") String feedId,
            @PathVariable("commentId") String commentId) {
        return ResponseEntity.ok(replyService.getRepliesForComment(feedId, commentId));
    }

}
