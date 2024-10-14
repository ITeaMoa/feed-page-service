package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.FeedEntity;
import com.example.demo.service.FeedService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createFeed(
    @RequestBody FeedEntity feedEntity, //요청본문에서 피드데이터 받고
    @RequestParam("feedType") String feedType) { //피드타입을 요청 매개변수로 받음
        //옵션이나 선택적 정보를 전달 피드타입은 url일부가 아니니까 매게변수 쓰는군
    feedService.createFeed(feedEntity, feedType);
    return ResponseEntity.ok("Feed created successfully");
}


    // 모든 피드 조회 엔드포인트 성공
    @GetMapping("/feeds-all")
    public ResponseEntity<List<FeedEntity>> getAllFeeds() {
        List<FeedEntity> feeds = feedService.getAllFeeds(); //모든 피드 조회
        return ResponseEntity.ok(feeds);
    }

   

    // 피드에 댓글 추가 엔드포인트
    @PostMapping("/{feedId}/{feedType}/comments")
    public ResponseEntity<String> addComment(
        @PathVariable("feedId") String feedId,  //피드 아이디를 경로변수 url일부로 피드타입과 다름 그래서 경로변수
        @PathVariable("feedType") String feedType, //타입도 경로변수
        @RequestBody Comment comment) { //요청본문에서 댓글데이터 받기
    try {
        feedService.addComment(feedId, feedType, comment); //아이디 타입 댓글 서비스계층 전달
        return ResponseEntity.ok("Comment added successfully");
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}

    // 피드 삭제 엔드포인트 
    @DeleteMapping("/{feedId}/{feedType}")
    public ResponseEntity<String> deleteFeed(@PathVariable("feedId") String feedId, @PathVariable("feedType") String feedType) {
    try {
        feedService.deleteFeed(feedId, feedType);
        return ResponseEntity.ok("Feed deleted successfully");
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}

    //좋아요 엔드포인트
    @PostMapping("/{feedId}/like")
public ResponseEntity<String> likeFeed(
        @PathVariable("feedId") String feedId,
        @RequestParam("userId") String userId,
        @RequestParam("feedType") String feedType) { // feedType이 필수 파라미터임
    feedService.likeFeed(userId, feedId, feedType);
    return ResponseEntity.ok("Like added successfully");
}

       
    


}

