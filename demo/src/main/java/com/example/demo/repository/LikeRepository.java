package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Like;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class LikeRepository {

    private final DynamoDbTable<Like> likeTable;

    public LikeRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.likeTable = enhancedClient.table("Mytable", TableSchema.fromBean(Like.class));
    }

    public void save(Like like) {
        likeTable.putItem(like);
    }
}


