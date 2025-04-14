package com.example.demo.entity;

import com.example.demo.constant.DynamoDbEntityType;
import com.example.demo.converter.LocalDateTimeConverter;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class Like extends BaseEntity {

    private String feedID;
    private String feedType; 

    @DynamoDbAttribute("entityType")
    public DynamoDbEntityType getEntityType() {
        return DynamoDbEntityType.LIKE;
    }

    @DynamoDbAttribute("feedID")
    public String getFeedID() {
        return feedID;
    }

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

    @DynamoDbAttribute("feedType")
    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    @Override
    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @DynamoDbAttribute("timestamp")
    public LocalDateTime getTimestamp() {
        return super.getTimestamp();
    }

    @Override
    @DynamoDbAttribute("creatorId")
    @DynamoDbSecondaryPartitionKey(indexNames = {"CreatorId-index"})
    public String getCreatorId() {
        return super.getCreatorId();
    }

    @Override
    @DynamoDbAttribute("userStatus")
    public Boolean getUserStatus() {
        return super.getUserStatus();
    }
}
