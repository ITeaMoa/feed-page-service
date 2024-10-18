package com.example.demo.repository;

import com.example.demo.entity.FeedEntity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;


@Repository    // aws 다이나모디비를 사용해 피드데이터를 crud하는 클래스입
public class DynamoDBFeedRepository implements FeedRepository {

    private final DynamoDbTable<FeedEntity> feedTable; //다이나모디비테이블
    private final DynamoDbIndex<FeedEntity> creatorIdIndex; //보조인덱스를 사용한 조회 아직 잘 모르겠음

    @Value("${dynamodb.table.name}")
    private String tableName;

    // 생성자에서 DynamoDbClient와 테이블 이름을 받아와 DynamoDB 테이블 및 인덱스 설정
    public DynamoDBFeedRepository(DynamoDbClient dynamoDbClient, @Value("${dynamodb.table.name}") String tableName) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient) // DynamoDB 클라이언트를 사용
                .build();
    
        this.feedTable = enhancedClient.table(tableName, TableSchema.fromBean(FeedEntity.class));
        this.creatorIdIndex = feedTable.index("CreatorID-index"); // 보조 인덱스 설정
    }

    @Override
    public void save(FeedEntity feedEntity) {
        feedTable.putItem(feedEntity); //피드저장 테이블에 데이터 삽입
    }

    @Override
    public FeedEntity findById(String id, String feedType) {
        Key key = Key.builder()
                .partitionValue("FEED#" + id) //피드의 고유 ID를 기반으로 파티션 키를 설정
                .sortValue("FEEDTYPE#" + feedType) //피드의 타입을 정렬 키로 사용
                .build();
        return feedTable.getItem(r -> r.key(key)); //주어진 키에 해당하는 아이템을 테이블에서 조회
    }

    @Override
    public List<FeedEntity> findAll() {
        SdkIterable<Page<FeedEntity>> results = feedTable.scan(); // 모든 피드를 스캔
        List<FeedEntity> feeds = new ArrayList<>();
        
        for (Page<FeedEntity> page : results) {
            feeds.addAll(page.items()); // 결과 페이지의 모든 항목을 리스트에 추가
        }

        return feeds;
    }

    @Override //보조인덱스를 사용해 특정 사용자가 작성한 피드 조회
    public List<FeedEntity> findByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());

        SdkIterable<Page<FeedEntity>> results = creatorIdIndex.query(queryConditional);
        List<FeedEntity> items = new ArrayList<>();

        for (Page<FeedEntity> page : results) {
            items.addAll(page.items());
        }

        return items;
    }

    @Override //사용자가 저장한 피드 필터링해서 조회
    public List<FeedEntity> findSavedFeedByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());

        Expression filterExpression = Expression.builder()
                .expression("SavedFeed = :saved") //true인 항목만 조회되도록 필터링
                .putExpressionValue(":saved", AttributeValue.builder().bool(true).build())
                .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .filterExpression(filterExpression)
                .build();

        SdkIterable<Page<FeedEntity>> results = creatorIdIndex.query(queryRequest);
        List<FeedEntity> items = new ArrayList<>();

        for (Page<FeedEntity> page : results) {
            items.addAll(page.items());
        }

        return items;
    }

    @Override
    public void delete(FeedEntity feedEntity) {
        Key key = Key.builder()
                .partitionValue(feedEntity.getPk())
                .sortValue(feedEntity.getSk())
                .build();
        feedTable.deleteItem(r -> r.key(key)); // 주어진 키에 해당하는 아이템을 삭제
    }
    
    
}

