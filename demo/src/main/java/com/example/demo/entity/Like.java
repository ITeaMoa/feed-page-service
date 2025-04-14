package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.entity.FeedEntity.LocalDateTimeConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Like {

    private String pk;  // 사용자별로 구분
    private String sk;  // 피드별 구분
    private String entityType = "LIKE";  // 
    private String feedID;
    private LocalDateTime timestamp;
    private Boolean userStatus;
    private String creatorId;
    private String feedType; //탈퇴한 사용자의 좋아요를  처리하기 위해서는 있어야됨

    
    @DynamoDbAttribute("feedType")
    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    @DynamoDbAttribute("userStatus")
    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    @DynamoDbAttribute("creatorId")
    public String getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("Pk")
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("Sk")
    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    @DynamoDbAttribute("feedID")
    public String getFeedID() {
        return feedID;
    }

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @DynamoDbAttribute("timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

