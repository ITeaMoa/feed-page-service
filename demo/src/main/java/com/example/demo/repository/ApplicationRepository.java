package com.example.demo.repository;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Application;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


@Repository
public class ApplicationRepository {
     private final DynamoDbTable<Application> applicationTable;

    public ApplicationRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.applicationTable = enhancedClient.table("IM_MAIN_TB", TableSchema.fromBean(Application.class));    }

    public void save(Application applicationEntity) {
        if (applicationEntity.getPk() == null || applicationEntity.getSk() == null) {
            throw new IllegalArgumentException("pk or sk can not null.");
        }
        applicationTable.putItem(applicationEntity);
    }

    // 특정 유저의 신청 내역 조회 일단 로그인 안해서 이렇게
    public List<Application> findByUserPk(String userPk) {
        return applicationTable.query(r -> r.queryConditional(
                software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo(Key.builder().partitionValue(userPk).build())
        )).items().stream().collect(Collectors.toList());
    }

    
}
