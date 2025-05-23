package com.example.demo.service;

import com.example.demo.constant.DynamoDbEntityType;
import com.example.demo.entity.Comment;
import com.example.demo.entity.FeedEntity;

import com.example.demo.entity.UserProfile;
import com.example.demo.repository.FeedRepository;
import com.example.demo.repository.UserProfileRepository;


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
    private final UserProfileRepository userProfileRepository;

    public FeedService(FeedRepository feedRepository, UserProfileRepository userProfileRepository) {
        this.feedRepository = feedRepository;
        this.userProfileRepository = userProfileRepository;
    }

    // 피드 생성 메서드
    public void createFeed(FeedEntity feedEntity, String feedType, String userId) {
        String feedId = UUID.randomUUID().toString(); // 랜덤 ID 생성

       //피드타입 대문자 변환합니다다
        String formattedFeedType = feedType.toUpperCase();

        feedEntity.setPk("FEED#" + feedId); 
        feedEntity.setSk("FEEDTYPE#" + formattedFeedType);
        feedEntity.setEntityType(DynamoDbEntityType.FEED); 
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

        // creatorId 기반으로 userStatus 가져오기
        if (userProfile != null) {
            feedEntity.setUserStatus(userProfile.getUserStatus() != null ? userProfile.getUserStatus() : true);
        } else {
            feedEntity.setUserStatus(true); // 기본값은 true
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
     // 게시물 수정 기능
     public void updateFeed(String feedId, String feedType, String userId, FeedEntity updatedFeed) {
        FeedEntity existingFeed = feedRepository.findById(feedId, feedType);
        if (existingFeed == null) {
            throw new RuntimeException("해당 피드를 찾을 수 없습니다.");
        }
    
        // 작성자 본인인지 확인
        if (!existingFeed.getCreatorId().equals("USER#" + userId)) {
            throw new RuntimeException("게시물 수정 권한이 없습니다.");
        }
    
        // 기존 필드를 유지하면서 새로운 데이터 반영
        existingFeed.setTitle(updatedFeed.getTitle() != null ? updatedFeed.getTitle() : existingFeed.getTitle());
        existingFeed.setContent(updatedFeed.getContent() != null ? updatedFeed.getContent() : existingFeed.getContent());
        existingFeed.setTags(updatedFeed.getTags() != null ? updatedFeed.getTags() : existingFeed.getTags());
        existingFeed.setPeriod(updatedFeed.getPeriod() != null ? updatedFeed.getPeriod() : existingFeed.getPeriod());
        existingFeed.setDeadline(updatedFeed.getDeadline() != null ? updatedFeed.getDeadline() : existingFeed.getDeadline());
        existingFeed.setImageUrl(
            updatedFeed.getImageUrl() != null ? updatedFeed.getImageUrl() : existingFeed.getImageUrl()
        );

        feedRepository.save(existingFeed);
    }
    
    // 피드 삭제 메서드
    public void deleteFeed(String feedId, String feedType, String userId) {
        // 피드 조회
        FeedEntity feedEntity = feedRepository.findById(feedId, feedType);
        if (feedEntity == null) {
            throw new RuntimeException("해당 피드를 찾을 수 없습니다.");
        }
    
        // 작성자인지 확인
        if (!feedEntity.getCreatorId().equals("USER#" + userId)) {
            throw new RuntimeException("게시물 삭제 권한이 없습니다.");
        }
    
        // 삭제 수행
        feedRepository.delete(feedEntity);
    }

    // 모든 피드 조회 메서드(나만의 테스트트)
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
    
                    
                    feedRepository.save(feed); 
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
    
        String commentId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
    
        String pk = "FEED#" + feedId;
        String sk = "COMMENT#" + commentId + "#" + now;
    
        // 유저 정보 조회
        String userPk = "USER#" + comment.getUserId();
        UserProfile userProfile = userProfileRepository.findById(userPk, "PROFILE#");
    
        // BaseEntity 상속 구조에 맞게 댓글 구성
        comment.setPk(pk);
        comment.setSk(sk);
        comment.setTimestamp(now);
        comment.setCreatorId(userPk);
        comment.setCommentId(commentId);
    
        if (userProfile != null) {
            comment.setNickname(userProfile.getNickname());
            comment.setUserStatus(userProfile.getUserStatus());
        } else {
            comment.setNickname("Unknown");
            comment.setUserStatus(true);
        }
    
        // 리스트가 null인 경우 초기화
        if (feedEntity.getComments() == null) {
            feedEntity.setComments(new ArrayList<>());
        }
    
        feedEntity.getComments().add(comment);
        feedRepository.save(feedEntity);
    }

    //  댓글 수정 메서드
    public void updateComment(String feedId, String feedType, String commentId, String userId, String newContent) {
    if (newContent == null || newContent.trim().isEmpty()) {
        throw new RuntimeException("수정할 댓글 내용을 입력해주세요.");
    }

    FeedEntity feedEntity = feedRepository.findById(feedId, feedType);
    if (feedEntity == null) {
        throw new RuntimeException("해당 피드를 찾을 수 없습니다.");
    }

    List<Comment> comments = feedEntity.getComments();
    if (comments != null) {
        for (Comment comment : comments) {
            if (comment.getCommentId().equals(commentId)) {
                if (!comment.getUserId().equals(userId)) {
                    throw new RuntimeException("댓글 수정 권한이 없습니다.");
                }

                comment.setComment(newContent);
                comment.setTimestamp(LocalDateTime.now()); // 수정된 시간 반영

                feedEntity.setComments(comments);
                feedRepository.save(feedEntity);
                return;
            }
        }
    }

    throw new RuntimeException("해당 댓글을 찾을 수 없습니다.");
}

    
    
    

    // 댓글 삭제 메서드
    public void deleteComment(String feedId, String feedType, String commentId, String userId) {
    FeedEntity feedEntity = feedRepository.findById(feedId, feedType);
    if (feedEntity == null) {
        throw new RuntimeException("해당 피드를 찾을 수 없습니다.");
    }

    List<Comment> comments = feedEntity.getComments();
    if (comments == null || comments.isEmpty()) {
        throw new RuntimeException("댓글이 존재하지 않습니다.");
    }

    boolean removed = comments.removeIf(comment ->
        comment.getCommentId().equals(commentId) &&
        comment.getUserId().equals(userId)
    );

    if (!removed) {
        throw new RuntimeException("댓글 삭제 권한이 없거나 댓글을 찾을 수 없습니다.");
    }

    feedEntity.setComments(comments);
    feedRepository.save(feedEntity);
}

    
    //신청청결과를 피드에 반영하는 로직임임
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
    
    //신청취소결과를 피드에 반영하는 로직임임
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




