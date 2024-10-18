package com.example.demo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamoDbBean
public class FeedEntity {

    private String pk;
    private String sk;
    private String entityType; 
    private String creatorId;
    private String title;
    private String content;
    private LocalDateTime timestamp;
    private Boolean postStatus;
    private Boolean savedFeed;
    private List<String> tags;
    private List<Comment> comments; 
    private Integer recruitmentNum;
    private LocalDateTime deadline;
    private String place;
    private Integer period;
    private Integer likesCount;
    private Map<String, Integer> applyNum;  // 신청자 수를 역할별로 관리할 맵
    private Map<String, Integer> roles; // 해쉬해쉬

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    @DynamoDbAttribute("EntityType")
    public String getEntityType() {
        return entityType;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"CreatorID-index"})
    @DynamoDbAttribute("CreatorID")
    public String getCreatorId() {
        return creatorId;
    }

    @DynamoDbAttribute("Title")
    public String getTitle() {
        return title;
    }

    @DynamoDbAttribute("Content")
    public String getContent() {
        return content;
    }

    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @DynamoDbAttribute("Timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @DynamoDbAttribute("PostStatus")
    public Boolean getPostStatus() {
        return postStatus;
    }

    @DynamoDbAttribute("SavedFeed") //임시저장상태
    public Boolean getSavedFeed() {
        return savedFeed;
    }

    @DynamoDbAttribute("Tags")
    public List<String> getTags() {
        return tags;
    }

    @DynamoDbAttribute("Comments")
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @DynamoDbAttribute("RecruitmentNum")
    public Integer getRecruitmentNum() {
        return recruitmentNum;
    }

    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @DynamoDbAttribute("Deadline")
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @DynamoDbAttribute("Place")
    public String getPlace() {
        return place;
    }

    @DynamoDbAttribute("Period")
    public Integer getPeriod() {
        return period;
    }

    @DynamoDbAttribute("LikesCount")
    public Integer getLikesCount() {
        return likesCount;
    }

    @DynamoDbAttribute("Roles")
    public Map<String, Integer> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Integer> roles) {
        this.roles = roles;
    }
    
    @DynamoDbAttribute("ApplyNum")
    public Map<String, Integer> getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(Map<String, Integer> applyNum) {
        this.applyNum = applyNum;
    }

    // LocalDateTimeConverter 클래스 정의
    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime> {

        @Override
        public AttributeValue transformFrom(LocalDateTime input) {
            if (input == null) {
                return null; // null 값을 명시적으로 저장하지 않음
            }
            return AttributeValue.builder().s(input.toString()).build();
        }

        @Override
        public LocalDateTime transformTo(AttributeValue attributeValue) {
            if (attributeValue == null || attributeValue.nul() != null && attributeValue.nul()) {
                return null;
            }
            return LocalDateTime.parse(attributeValue.s());
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


