package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.constant.DynamoDbEntityType;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Setter
@DynamoDbBean
public abstract class BaseEntity {
    private String pk;
    private String sk;
    private DynamoDbEntityType entityType;
    private LocalDateTime timestamp;
    private Boolean userStatus;
    private String creatorId;
    
    public BaseEntity() {}

    @DynamoDbPartitionKey
    @DynamoDbAttribute("Pk")
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("Sk")
    @DynamoDbSecondaryPartitionKey(indexNames = {"MostLikedFeed-index", "PostedFeed-index", "Application-index"})
    @DynamoDbSecondarySortKey(indexNames = {"SearchByCreator-index"})
    public String getSk() {
        return sk;
    }

    @DynamoDbAttribute("entityType")
    @DynamoDbSecondarySortKey(indexNames = "Like-index")
    public DynamoDbEntityType getEntityType(){
        return entityType;
    }


    @DynamoDbAttribute("timestamp")
    @DynamoDbSecondarySortKey(indexNames = "PostedFeed-index")
    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    @DynamoDbAttribute("creatorId")
    @DynamoDbSecondaryPartitionKey(indexNames = {"SearchByCreator-index", "CreatorId-index"})
    public String getCreatorId(){
        return creatorId;
    }

    @DynamoDbAttribute("userStatus")
    public Boolean getUserStatus(){
        return userStatus;
    }
    
    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }
}
