package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.FeedEntity;
import com.example.demo.service.FeedService;
import com.example.demo.service.S3Service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;
    private final S3Service s3Service;
 

    public FeedController(FeedService feedService, S3Service s3Service) {
        this.feedService = feedService;
        this.s3Service = s3Service;

    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("hello feedpage");
    }

    //피드 생성 엔드포인트트
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createFeed(
        @RequestPart("feed") String feedJson,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @RequestParam("feedType") String feedType,
        @RequestParam("userId") String userId) {

    try {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 

        FeedEntity feedEntity = objectMapper.readValue(feedJson, FeedEntity.class);

        if (image != null && !image.isEmpty()) {
            String imageUrl = s3Service.uploadFile(image);
            feedEntity.setImageUrl(imageUrl);
        }

        feedService.createFeed(feedEntity, feedType, userId);
        return ResponseEntity.ok("Feed created successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Feed creation failed: " + e.getMessage());
    }
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
    // 피드 수정 메소드
    @PutMapping(value = "/{feedId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateFeed(
            @PathVariable("feedId") String feedId,
            @RequestParam("feedType") String feedType,
            @RequestParam("userId") String userId,
            @RequestPart("feed") String feedJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            FeedEntity updatedFeed = objectMapper.readValue(feedJson, FeedEntity.class);

            // 이미지 업로드
            if (image != null && !image.isEmpty()) {
                String imageUrl = s3Service.uploadFile(image);
                updatedFeed.setImageUrl(imageUrl);
            }

            feedService.updateFeed(feedId, feedType, userId, updatedFeed);
            return ResponseEntity.ok("Feed updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Feed update failed: " + e.getMessage());
        }
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

       
    


}

