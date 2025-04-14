package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.FeedService;
import com.example.demo.service.LikeService;

@RestController
@RequestMapping("/feed")
public class LikeController {
    private final FeedService feedService;
    private final LikeService likeService;
 

    public LikeController(FeedService feedService, LikeService likeService) {
        this.feedService = feedService;
        this.likeService=likeService;
    }

    //좋아요 엔드포인트 업데이트로 바꿨습니다
    @PutMapping("/{feedId}/like")
    public ResponseEntity<String> likeFeed(
    @PathVariable("feedId") String feedId,
    @RequestParam("userId") String userId,
    @RequestParam("feedType") String feedType) {
    likeService.likeFeed(userId, feedId, feedType);
    return ResponseEntity.ok("Like added successfully");
}
    @DeleteMapping("/{feedId}/like")
public ResponseEntity<String> unlikeFeed(
    @PathVariable("feedId") String feedId,
    @RequestParam("userId") String userId,
    @RequestParam("feedType") String feedType
) {
    likeService.unlikeFeed(userId, feedId, feedType);
    return ResponseEntity.ok("좋아요가 취소되었습니다.");
}
}
