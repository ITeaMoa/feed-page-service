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

    // ✅ 좋아요 추가
    public void likeFeed(String userId, String feedId, String feedType) {
        Like existingLike = likeRepository.findLikeByUserAndFeed(userId, feedId);
        if (existingLike != null) {
            throw new RuntimeException("이미 좋아요한 피드입니다.");
        }

        Like like = new Like();
        like.setPk("USER#" + userId);
        like.setSk("LIKE#" + feedId);
        like.setFeedID(feedId);
        like.setFeedType(feedType);
        like.setCreatorId("USER#" + userId);
        like.setUserStatus(true);
        like.setTimestamp(LocalDateTime.now());

        likeRepository.save(like);

        FeedEntity feed = feedRepository.findById(feedId, feedType);
        if (feed == null) {
            throw new RuntimeException("피드를 찾을 수 없습니다.");
        }

        feed.setLikesCount(feed.getLikesCount() + 1);
        feedRepository.save(feed);
    }

    // ✅ 좋아요 취소
    public void unlikeFeed(String userId, String feedId, String feedType) {
        Like existingLike = likeRepository.findLikeByUserAndFeed(userId, feedId);
        if (existingLike == null) {
            throw new RuntimeException("좋아요 내역이 없습니다.");
        }

        likeRepository.delete(existingLike);

        FeedEntity feed = feedRepository.findById(feedId, feedType);
        if (feed == null) {
            throw new RuntimeException("피드를 찾을 수 없습니다.");
        }

        int currentLikes = feed.getLikesCount() != null ? feed.getLikesCount() : 0;
        feed.setLikesCount(Math.max(currentLikes - 1, 0));
        feedRepository.save(feed);
    }

    // ✅ 탈퇴 유저의 좋아요 정리 (userStatus=false인 경우 삭제)
    public void cleanUpLikesByDeletedUser(String userId) {
        String userPk = "USER#" + userId;
        List<Like> likesByUser = likeRepository.findAllByUserPk(userPk);

        for (Like like : likesByUser) {
            if (Boolean.FALSE.equals(like.getUserStatus())) {
                likeRepository.delete(like);
            }
        }
    }
}
