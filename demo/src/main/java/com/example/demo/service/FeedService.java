package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.FeedEntity;
import com.example.demo.entity.Like;
import com.example.demo.repository.FeedRepository;
import com.example.demo.repository.LikeRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final LikeRepository likeRepository;

    public FeedService(FeedRepository feedRepository, LikeRepository likeRepository) {
        this.feedRepository = feedRepository;
        this.likeRepository = likeRepository;
    }

    // 피드 생성 메서드
    public void createFeed(FeedEntity feedEntity, String feedType) {
        String feedId = UUID.randomUUID().toString(); // 랜덤 ID 생성
        feedEntity.setPk("FEED#" + feedId); // PK 설정
    
        // 여기서 feedType을 받아서 명시적으로 SK에 설정
        feedEntity.setSk("FEEDTYPE#" + feedType); 
    
        feedEntity.setTimestamp(LocalDateTime.now()); // 현재 시간 설정
        feedEntity.setLikesCount(0);  // 좋아요 수 초기화

        // applyNum을 빈 Map으로 초기화 
        feedEntity.setApplyNum(new HashMap<>()); // 빈 HashMap으로 초기화
    
        if (feedEntity.getComments() == null) {
            feedEntity.setComments(new ArrayList<>()); // 댓글 리스트 초기화
        }

        if(feedEntity.getRoles() == null) {
            feedEntity.setRoles(new HashMap<>());
        }
    
        // 피드 저장
        feedRepository.save(feedEntity);
    }


    // 모든 피드 조회 메서드
    public List<FeedEntity> getAllFeeds() {
        return feedRepository.findAll().stream()
            .filter(feed -> feed.getPk().startsWith("FEED#"))
            .collect(Collectors.toList());
    }


    // 피드에 댓글 추가 메서드
    public void addComment(String feedId, String feedType, Comment comment) {
        // 피드를 feedId와 feedType으로 조회
        FeedEntity feedEntity = feedRepository.findById(feedId, feedType);
        
        if (feedEntity == null) {
            throw new RuntimeException("Feed not found");
        }
    
        // 댓글 리스트가 없으면 새 리스트 생성
        if (feedEntity.getComments() == null) {
            feedEntity.setComments(new ArrayList<>());
        }
    
        // 댓글에 현재 시간 추가
        comment.setTimestamp(LocalDateTime.now());
    
        // 댓글 리스트에 새로운 댓글 추가
        feedEntity.getComments().add(comment);
    
        // 피드 엔티티 업데이트
        feedRepository.save(feedEntity);
    }

    

    // 피드 삭제 메서드
    public void deleteFeed(String feedId, String feedType) {
        FeedEntity feedEntity = feedRepository.findById(feedId, feedType);
        if (feedEntity != null) {
            feedRepository.delete(feedEntity);
        } else {
            throw new RuntimeException("Feed not found");
        }
    }

    public void likeFeed(String userId, String feedId, String feedType) {
        // 좋아요 엔티티 생성
        Like existingLike = likeRepository.findLikeByUserAndFeed(userId, feedId);
        
        if (existingLike != null) {
            // 이미 좋아요가 존재하는 경우
            throw new RuntimeException("User already liked this feed");
        }
    
        Like like = new Like(
            "USER#" + userId,
            "LIKE#" + feedId,
            "Like",
            feedId,
            LocalDateTime.now()
        );
    
        likeRepository.save(like);
    
        // 피드 조회 및 좋아요 수 증가
        FeedEntity feed = feedRepository.findById(feedId, feedType); 
        if (feed == null) {
            throw new RuntimeException("Feed not found");
        }
        
        feed.setLikesCount(feed.getLikesCount() + 1);
        feedRepository.save(feed);
    }
    

    


    public void applyToFeed(String feedId, String part, String feedType) {
        FeedEntity feedEntity = feedRepository.findById(feedId, feedType);
        
        if (feedEntity == null) {
            throw new RuntimeException("피드를 찾을 수 없습니다.");
        }

        Map<String, Integer> applyNum = feedEntity.getApplyNum();
        if (applyNum == null) {
            applyNum = new HashMap<>(); //신청자수가 없으니 새로운 맵
        }

        applyNum.put(part, applyNum.getOrDefault(part, 0) + 1); //각 파트에 맞는 분야 신청시 추가가 됨
        feedEntity.setApplyNum(applyNum);
        feedRepository.save(feedEntity);
    }
    

}



