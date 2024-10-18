package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.FeedEntity;

public interface FeedRepository {
    void save(FeedEntity feedEntity);
    FeedEntity findById(String id, String feedType); // feedType 파라미터 추가
    List<FeedEntity> findAll();
    List<FeedEntity> findByUserId(String userId);
    List<FeedEntity> findSavedFeedByUserId(String userId);
    void delete(FeedEntity feedEntity);

}

