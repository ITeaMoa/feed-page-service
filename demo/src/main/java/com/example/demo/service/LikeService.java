package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.FeedEntity;
import com.example.demo.entity.Like;
import com.example.demo.repository.FeedRepository;
import com.example.demo.repository.LikeRepository;


@Service
public class LikeService {

    private final FeedRepository feedRepository;
    private final LikeRepository likeRepository;

    public LikeService(FeedRepository feedRepository, LikeRepository likeRepository) {
        this.feedRepository = feedRepository;
        this.likeRepository = likeRepository;
    }

    public void likeFeed(String userId, String feedId, String feedType) {
        // 이미 좋아요 했는지 확인
        Like existingLike = likeRepository.findLikeByUserAndFeed(userId, feedId);
        if (existingLike != null) {
            throw new RuntimeException("User already liked this feed");
        }

        // 좋아요 객체 생성
        Like like = new Like();
        like.setPk("USER#" + userId);  // 유저 기준 PK
        like.setSk("LIKE#" + feedId);  // 피드 기준 SK
        like.setEntityType("LIKE");
        like.setFeedID(feedId);
        like.setFeedType(feedType); // 
        like.setTimestamp(LocalDateTime.now());
        like.setUserStatus("ACTIVE");  // 추가된 필드
        like.setCreatorId("USER#" + userId);  // 추가된 필드

        likeRepository.save(like);

        // 피드에 좋아요 수 증가
        FeedEntity feed = feedRepository.findById(feedId, feedType);
        if (feed == null) {
            throw new RuntimeException("Feed not found");
        }

        feed.setLikesCount(feed.getLikesCount() + 1);
        feedRepository.save(feed);
    }

    // 좋아요 취소 기능 
    public void unlikeFeed(String userId, String feedId, String feedType) {
        Like existingLike = likeRepository.findLikeByUserAndFeed(userId, feedId);
        if (existingLike == null) {
            throw new RuntimeException("좋아요 기록이 없습니다.");
        }


        likeRepository.delete(existingLike);

        // 좋아요 수 감소
        FeedEntity feed = feedRepository.findById(feedId, feedType);
        if (feed == null) {
            throw new RuntimeException("Feed not found");
        }

        int currentLikes = feed.getLikesCount() != null ? feed.getLikesCount() : 0;
        feed.setLikesCount(Math.max(currentLikes - 1, 0)); // 0 밑으로 내려가지 않게
        feedRepository.save(feed);
    }


    //탈퇴유저의 좋아요 수 취소 이 메소드는 나중에 유저서비스에서 탈퇴서비스를 만들때 수행할 예정정
    public void cleanUpLikesByDeletedUser(String userId) {
        String userPk = "USER#" + userId;
        List<Like> likesByUser = likeRepository.findAllByUserPk(userPk);

        for (Like like : likesByUser) {
            if ("DELETED".equals(like.getUserStatus())) {
                String feedId = like.getFeedID();
                String feedType = like.getFeedType(); // feedType 활용

                FeedEntity feed = feedRepository.findById(feedId, feedType);
                if (feed != null) {
                    int currentLikes = feed.getLikesCount() != null ? feed.getLikesCount() : 0;
                    feed.setLikesCount(Math.max(currentLikes - 1, 0));
                    feedRepository.save(feed);
                }

                likeRepository.delete(like);
            }
        }
    }
    
}
