package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.FeedEntity;
import com.example.demo.entity.Like;
import com.example.demo.entity.UserProfile;
import com.example.demo.repository.FeedRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.UserProfileRepository;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final LikeRepository likeRepository;
    private final UserProfileRepository userProfileRepository;

    public FeedService(FeedRepository feedRepository, LikeRepository likeRepository, UserProfileRepository userProfileRepository) {
        this.feedRepository = feedRepository;
        this.likeRepository = likeRepository;
        this.userProfileRepository = userProfileRepository;
    }

    // 피드 생성 메서드
    public void createFeed(FeedEntity feedEntity, String feedType, String userId) {
        String feedId = UUID.randomUUID().toString(); // 랜덤 ID 생성

       //피드타입 대문자 변환합니다다
        String formattedFeedType = feedType.toUpperCase();

        feedEntity.setPk("FEED#" + feedId); 
        feedEntity.setSk("FEEDTYPE#" + formattedFeedType);
        feedEntity.setEntityType("FEED");
        feedEntity.setTimestamp(LocalDateTime.now()); 
        feedEntity.setLikesCount(0);  

         // userId를 직접 creatorId로 설정
    if (userId != null) {
        feedEntity.setCreatorId("USER#"+userId);
    } else {
        throw new RuntimeException("userId가 null입니다. 확인이 필요합니다.");
    }

        // creatorId 기반으로 nickname 가져오기
        String userPk = "USER#" + userId;
        UserProfile userProfile = userProfileRepository.findById(userPk, "PROFILE#");
        if (userProfile != null && userProfile.getNickname() != null) {
            feedEntity.setNickname(userProfile.getNickname()); // 닉네임 저장
        } else {
            feedEntity.setNickname("Unknown"); // 닉네임이 없을 경우 기본값
        }
    
        // Roles r기반으로  RecruitmentRoles 초기화
        Map<String, Integer> recruitmentRoles = new HashMap<>();
        if (feedEntity.getRoles() != null) {
            for (String role : feedEntity.getRoles().keySet()) {
                recruitmentRoles.put(role, 0); 
            }
        }
        feedEntity.setRecruitmentRoles(recruitmentRoles); // 
    
        if (feedEntity.getComments() == null) {
            feedEntity.setComments(new ArrayList<>()); 
        }
    
        
        feedRepository.save(feedEntity);
    }
    

    // 모든 피드 조회 메서드
    public List<FeedEntity> getAllFeeds() {
        return feedRepository.findAll().stream()
            .filter(feed -> feed.getPk().startsWith("FEED#"))
            .map(feed -> {
                if (feed.getComments() != null) {
                    List<Comment> updatedComments = feed.getComments().stream()
                        .map(comment -> {
                            
                            if (comment.getUserId() != null) {
                                String userPk = "USER#" + comment.getUserId();
                                UserProfile userProfile = userProfileRepository.findById(userPk, "PROFILE#");
                                if (userProfile != null) {
                                    comment.setNickname(userProfile.getNickname());
                                } else {
                                    comment.setNickname("Unknown");
                                }
                            }
                            return comment;
                        })
                        .collect(Collectors.toList());
                    feed.setComments(updatedComments);
                }
                return feed;
            })
            .collect(Collectors.toList());
    }
    



    // 피드에 댓글 추가 메서드
    public void addComment(String feedId, String feedType, Comment comment) {
       
        FeedEntity feedEntity = feedRepository.findById(feedId, feedType);
        if (feedEntity == null) {
            throw new RuntimeException("해당 피드를 찾을 수 없습니다.");
        }
    
       
        comment.setTimestamp(LocalDateTime.now());
    
        
        String userPk = "USER#" + comment.getUserId();
        UserProfile userProfile = userProfileRepository.findById(userPk, "INFO#");
        if (userProfile != null && userProfile.getNickname() != null) {
            comment.setNickname(userProfile.getNickname());
        } else {
            comment.setNickname("Unknown");
        }
    
  
        if (feedEntity.getComments() == null) {
            feedEntity.setComments(new ArrayList<>());
        }
    
      
        if (comment.getUserId() == null) {
            throw new RuntimeException("userId가 null입니다. 확인이 필요합니다.");
        }
    
        feedEntity.getComments().add(comment);
    
     
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
            "LIKE",
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

        Map<String, Integer> recruitmentRoles = feedEntity.getRecruitmentRoles();
        if (recruitmentRoles == null) {
            recruitmentRoles = new HashMap<>(); //신청자수가 없으니 새로운 맵
        }

        recruitmentRoles.put(part, recruitmentRoles.getOrDefault(part, 0) + 1); //각 파트에 맞는 분야 신청시 추가가 됨
        feedEntity.setRecruitmentRoles(recruitmentRoles);
        feedRepository.save(feedEntity);
    }

    public FeedEntity findFeedByPk(String pk) {
        // STUDY 타입으로 조회
        FeedEntity feedEntity = feedRepository.findFeedByPkAndSk(pk, "FEEDTYPE#STUDY");
        if (feedEntity != null && feedEntity.getNickname() != null) {
            return feedEntity;
        }
    
        // PROJECT 타입으로 조회
        feedEntity = feedRepository.findFeedByPkAndSk(pk, "FEEDTYPE#PROJECT");
        if (feedEntity != null && feedEntity.getNickname() != null) {
            return feedEntity;
        }
    
        // 둘 다 없으면 null 반환
        System.err.println("No feed found with PK: " + pk + " and SK: FEEDTYPE#STUDY/PROJECT");
        return null;
    }
    
    public void cancelApplicationInFeed(String feedId, String part) {
        
        FeedEntity feedEntity = findFeedByPk("FEED#" + feedId);
    
        if (feedEntity == null) {
            throw new RuntimeException("피드를 찾을 수 없습니다.");
        }
    
        // 신청자 수 감소
        Map<String, Integer> recruitmentRoles = feedEntity.getRecruitmentRoles();
        if (recruitmentRoles != null && recruitmentRoles.containsKey(part)) {
            int currentCount = recruitmentRoles.get(part);
            recruitmentRoles.put(part, Math.max(currentCount - 1, 0));  // 0 이하로 내려가지 않게
        }
    
        feedEntity.setRecruitmentRoles(recruitmentRoles);
        feedRepository.save(feedEntity);
    }
    
    
    
    
    

}




