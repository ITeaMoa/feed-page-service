package com.example.demo.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.ReplyEntity;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class ReplyRepository {

    private final DynamoDbTable<ReplyEntity> replyTable;

    public ReplyRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.replyTable = enhancedClient.table("IM_MAIN_TB", TableSchema.fromBean(ReplyEntity.class));
    }

    public void save(ReplyEntity replyEntity) {
        replyTable.putItem(replyEntity);
    }

    public void delete(ReplyEntity replyEntity) { //보류류일단보류이히히히
        Key key = Key.builder()
                .partitionValue(replyEntity.getPk())
                .sortValue(replyEntity.getSk())
                .build();
        replyTable.deleteItem(key);
    }

    public List<ReplyEntity> findByFeedIdAndCommentId(String feedId, String commentId) {
        String feedPk = "FEED#" + feedId;
        String skPrefix = "COMMENT#" + commentId + "#REPLY#";

        QueryConditional queryConditional = QueryConditional.keyEqualTo(k -> k.partitionValue(feedPk));

        return replyTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .stream()
                .filter(reply -> reply.getSk().startsWith(skPrefix))
                .collect(Collectors.toList());
    }
}
