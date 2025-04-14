package com.example.demo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;

import com.example.demo.constant.DynamoDbEntityType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class ReplyEntity extends BaseEntity {

    private String feedId;
    private String commentId;
    private String replyId;
    private String userId;
    private String content;
    private String nickname;

    @DynamoDbAttribute("entityType")
    public DynamoDbEntityType getEntityType() {
        return DynamoDbEntityType.REPLY;
    }

    @DynamoDbAttribute("feedId")
    public String getFeedId() {
        return feedId;
    }

    @DynamoDbAttribute("commentId")
    public String getCommentId() {
        return commentId;
    }

    @DynamoDbAttribute("replyId")
    public String getReplyId() {
        return replyId;
    }

    @DynamoDbAttribute("userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("content")
    public String getContent() {
        return content;
    }

    @DynamoDbAttribute("nickname")
    public String getNickname() {
        return nickname;
    }

    @Override
    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @DynamoDbAttribute("timestamp")
    public LocalDateTime getTimestamp() {
        return super.getTimestamp();
    }

    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime> {
        @Override
        public AttributeValue transformFrom(LocalDateTime input) {
            return input != null ? AttributeValue.builder().s(input.toString()).build() : null;
        }

        @Override
        public LocalDateTime transformTo(AttributeValue attributeValue) {
            return attributeValue != null && attributeValue.s() != null
                    ? LocalDateTime.parse(attributeValue.s())
                    : null;
        }

        @Override
        public EnhancedType<LocalDateTime> type() {
            return EnhancedType.of(LocalDateTime.class);
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.S;
        }
    }
}
