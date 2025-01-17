package com.example.demo.repository;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Application;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
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

   public List<Application> findByUserPk(String userPk) {
        
        QueryConditional queryConditional = QueryConditional.keyEqualTo(k -> k.partitionValue(userPk));

        
        return applicationTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .stream()
                .filter(item -> item.getSk().startsWith("APPLICATION#")) // `APPLICATION#`으로 필터링
                .collect(Collectors.toList());
    }

    public void delete(Application applicationEntity) {
        if (applicationEntity.getPk() == null || applicationEntity.getSk() == null) {
            throw new IllegalArgumentException("pk or sk can not be null.");
        }
        applicationTable.deleteItem(applicationEntity);
    }

    
}
