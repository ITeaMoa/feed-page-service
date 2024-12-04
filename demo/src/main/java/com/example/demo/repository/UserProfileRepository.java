package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.UserProfile;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class UserProfileRepository {
    private final DynamoDbTable<UserProfile> userProfileTable;

    public UserProfileRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.userProfileTable = enhancedClient.table("IM_MAIN_TB", TableSchema.fromBean(UserProfile.class));
    }

    public UserProfile findById(String pk, String sk) {
        Key key = Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build();
        return userProfileTable.getItem(r -> r.key(key));
    }
}
