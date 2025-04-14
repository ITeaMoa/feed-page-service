package com.example.demo.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Like;



import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class LikeRepository {

    private final DynamoDbTable<Like> likeTable;

    public LikeRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.likeTable = enhancedClient.table("IM_MAIN_TB", TableSchema.fromBean(Like.class));
    }

    // 좋아요 저장
    public void save(Like like) {
        likeTable.putItem(like);
    }

    // 좋아요 정보 조회 (userId + feedId 기준)
    public Like findLikeByUserAndFeed(String userId, String feedId) {
        Key key = Key.builder()
                .partitionValue("USER#" + userId)
                .sortValue("LIKE#" + feedId)
                .build();
        return likeTable.getItem(r -> r.key(key));
    }

    // 좋아요 삭제
    public void delete(Like like) {
        Key key = Key.builder()
                .partitionValue(like.getPk())
                .sortValue(like.getSk())
                .build();
        likeTable.deleteItem(key);
    }  

    public List<Like> findAllByUserPk(String userPk) {
    return likeTable.scan()
        .items()
        .stream()
        .filter(like -> like.getPk().equals(userPk))
        .collect(Collectors.toList());
}

}


