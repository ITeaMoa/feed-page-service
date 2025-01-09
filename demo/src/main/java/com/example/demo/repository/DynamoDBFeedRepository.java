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




@Repository
public class DynamoDBFeedRepository implements FeedRepository {

    private final DynamoDbTable<FeedEntity> feedTable;
    private final DynamoDbIndex<FeedEntity> mostLikedFeedIndex;
    private final DynamoDbIndex<FeedEntity> postedFeedIndex;

    public DynamoDBFeedRepository(DynamoDbClient dynamoDbClient, @Value("${dynamodb.table.name}") String tableName) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.feedTable = enhancedClient.table(tableName, TableSchema.fromBean(FeedEntity.class));
        this.mostLikedFeedIndex = feedTable.index("MostLikedFeed-index");
        this.postedFeedIndex = feedTable.index("PostedFeed-index");
    }

    @Override
    public void save(FeedEntity feedEntity) {
        feedTable.putItem(feedEntity);
    }

    @Override
    public FeedEntity findById(String id, String feedType) {
        Key key = Key.builder()
                .partitionValue("FEED#" + id)
                .sortValue("FEEDTYPE#" + feedType)
                .build();
        return feedTable.getItem(r -> r.key(key));
    }

    @Override
    public List<FeedEntity> findAll() {
        SdkIterable<Page<FeedEntity>> results = feedTable.scan();
        List<FeedEntity> feeds = new ArrayList<>();

        for (Page<FeedEntity> page : results) {
            feeds.addAll(page.items());
        }

        return feeds;
    }

    @Override
    public List<FeedEntity> findByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue("USER#" + userId).build());

        SdkIterable<Page<FeedEntity>> results = postedFeedIndex.query(queryConditional);
        List<FeedEntity> items = new ArrayList<>();

        for (Page<FeedEntity> page : results) {
            items.addAll(page.items());
        }

    return items;
}

// 좋아요 순 조회 메서드
public List<FeedEntity> findMostLikedFeeds() {
    QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue("FEED").build());

    SdkIterable<Page<FeedEntity>> results = mostLikedFeedIndex.query(r -> r.queryConditional(queryConditional).scanIndexForward(false));
    List<FeedEntity> items = new ArrayList<>();

    for (Page<FeedEntity> page : results) {
        items.addAll(page.items());
    }

    return items;
}

    @Override
    public List<FeedEntity> findSavedFeedByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue("USER#" + userId).build());

        Expression filterExpression = Expression.builder()
                .expression("SavedFeed = :saved")
                .putExpressionValue(":saved", AttributeValue.builder().bool(true).build())
                .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .filterExpression(filterExpression)
                .build();

        SdkIterable<Page<FeedEntity>> results = postedFeedIndex.query(queryRequest);
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
        feedTable.deleteItem(r -> r.key(key));
    }

    @Override
    public FeedEntity findFeedByPkAndSk(String pk, String sk) {
        Key key = Key.builder().partitionValue(pk).sortValue(sk).build();
        return feedTable.getItem(r -> r.key(key));
    }



}



