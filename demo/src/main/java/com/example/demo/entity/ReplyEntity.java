package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReplyEntity {
    private String pk; // FEED#<FeedID>
    private String sk; // COMMENT#<CommentID>#REPLY#<ReplyID>#<Timestamp>
    private String entityType = "REPLY";
    private String feedId;
    private String commentId;
    private String replyId;
    private String userId;
    private String content;
    private LocalDateTime timestamp;
    private String nickname;
    private Boolean userStatus;


    @DynamoDbAttribute("userStatus") 
    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute("Pk")
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("Sk")
    public String getSk() {
        return sk;
    }

    @DynamoDbAttribute("entityType")
    public String getEntityType() {
        return entityType;
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

    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @DynamoDbAttribute("timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @DynamoDbAttribute("nickname")
    public String getNickname() {
        return nickname;
    }

    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime> {

        @Override
        public AttributeValue transformFrom(LocalDateTime input) {
            if (input == null) {
                return null; 
            }
            return AttributeValue.builder().s(input.toString()).build(); //localdatetime을 문자열로 변환
        }

        @Override
        public LocalDateTime transformTo(AttributeValue attributeValue) {
            if (attributeValue == null || attributeValue.nul() != null && attributeValue.nul()) {
                return null;
            }
            return LocalDateTime.parse(attributeValue.s()); //문자열을 LocalDateTime으로 변환
        }

        @Override
        public EnhancedType<LocalDateTime> type() {
            return EnhancedType.of(LocalDateTime.class); // 변환할 타입을 정의
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.S; // DynamoDB에서 문자열로 처리
        }
    }

    
}
