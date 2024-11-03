package com.example.demo.repository;

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

    public void save(Like like) {
        likeTable.putItem(like);
    }

    public boolean existsByUserIdAndFeedId(String userId, String feedId) { //사용자가 피드에 좋아요 눌렀는지 확인
        Key key = Key.builder()
                .partitionValue("USER#" + userId)
                .sortValue("LIKE#" + feedId)
                .build();
    
        Like like = likeTable.getItem(r -> r.key(key));
        return like != null;
    }
    //사용자가 좋아요 눌렀는지 확인하고 like객체 반환 위에랑 중복되긴함
    public Like findLikeByUserAndFeed(String userId, String feedId) { 
        Key key = Key.builder() 
            .partitionValue("USER#" + userId)
            .sortValue("LIKE#" + feedId)
            .build();
        
        return likeTable.getItem(r -> r.key(key));
    }
}


