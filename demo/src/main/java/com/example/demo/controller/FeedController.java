package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.FeedEntity;
import com.example.demo.service.FeedService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;
 

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("hello feedpage");
    }

    //피드 생성 엔드포인트트
    @PostMapping("/create")
    public ResponseEntity<String> createFeed(
    @RequestBody FeedEntity feedEntity, //요청본문에서 피드데이터 받고
    @RequestParam("feedType") String feedType,
    @RequestParam("userId") String userId // userId 추가
    ) { //피드타입을 요청 매개변수로 받음
    
        //옵션이나 선택적 정보를 전달 피드타입은 url일부가 아니니까 매게변수 쓰는군

    feedService.createFeed(feedEntity, feedType, userId);
    return ResponseEntity.ok("Feed created successfully");
}
    // 피드 삭제 엔드포인트 
    @DeleteMapping("/{feedId}")
public ResponseEntity<String> deleteFeed(
    @PathVariable("feedId") String feedId,
    @RequestParam("feedType") String feedType,
    @RequestParam("userId") String userId
) {
    try {
        feedService.deleteFeed(feedId, feedType, userId);
        return ResponseEntity.ok("Feed deleted successfully");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PutMapping("/{feedId}")
public ResponseEntity<String> updateFeed(
    @PathVariable("feedId") String feedId,
    @RequestParam("feedType") String feedType,
    @RequestParam("userId") String userId,  // 작성자 확인을 위해 추가
    @RequestBody FeedEntity updatedFeed
) {
    feedService.updateFeed(feedId, feedType, userId, updatedFeed);
    return ResponseEntity.ok("Feed updated successfully");
}

    


    // 모든 피드 조회 엔드포인트 성공(나만의 테스트트)
    @GetMapping("/feeds-all")
    public ResponseEntity<List<FeedEntity>> getAllFeeds() {
        List<FeedEntity> feeds = feedService.getAllFeeds(); 
        return ResponseEntity.ok(feeds);
    }

   

    // 피드 댓글 추가 엔드포인트
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<String> addComment(
        @PathVariable("feedId") String feedId,
        @RequestParam("feedType") String feedType,
        @RequestBody Map<String, String> request) {

    String userId = request.get("userId");
    String commentContent = request.get("comment");

   
    if (userId == null || commentContent == null) {
        return ResponseEntity.badRequest().body("유저 ID와 댓글 내용은 필수입니다.");
    }

    // 댓글 생성
    Comment comment = new Comment();
    comment.setUserId(userId); //
    comment.setComment(commentContent);


    feedService.addComment(feedId, feedType, comment);

    return ResponseEntity.ok("댓글이 추가되었습니다.");
}

@PutMapping("/{feedId}/comments/{commentId}")
public ResponseEntity<String> updateComment(
    @PathVariable("feedId") String feedId,
    @RequestParam("feedType") String feedType,
    @PathVariable("commentId") String commentId,
    @RequestParam("userId") String userId,
    @RequestBody Map<String, String> request
) {
    String newContent = request.get("newContent");
    feedService.updateComment(feedId, feedType, commentId, userId, newContent);
    return ResponseEntity.ok("Comment updated successfully");
}

@DeleteMapping("/{feedId}/comments/{commentId}")
public ResponseEntity<String> deleteComment(
    @PathVariable("feedId") String feedId,
    @PathVariable("commentId") String commentId,
    @RequestParam("feedType") String feedType,
    @RequestParam("userId") String userId
) {
    try {
        feedService.deleteComment(feedId, feedType, commentId, userId);
        return ResponseEntity.ok("Comment deleted successfully");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


    //좋아요 엔드포인트 업데이트로 바꿨습니다
    @PutMapping("/{feedId}/like")
    public ResponseEntity<String> likeFeed(
    @PathVariable("feedId") String feedId,
    @RequestParam("userId") String userId,
    @RequestParam("feedType") String feedType) {
    feedService.likeFeed(userId, feedId, feedType);
    return ResponseEntity.ok("Like added successfully");
}
       
    


}


