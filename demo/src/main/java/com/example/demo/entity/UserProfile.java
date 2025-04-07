package com.example.demo.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import java.util.List;

@DynamoDbBean
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String pk;  
    private String sk;
    private String nickname;
    private String avatarUrl; //프로필사진진
    private String headLine;
    private List<String> tags;
    private List<String> educations;
    private List<String> personalUrl;
    private List<String> experiences;
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

    @DynamoDbAttribute("nickname") // getter 이름 수정
    public String getNickname() {
        return nickname;
    }
    @DynamoDbAttribute("avatarUrl")
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @DynamoDbAttribute("headLine")
    public String getHeadLine() {
        return headLine;
    }

    @DynamoDbAttribute("tags")
    public List<String> getTags() {
        return tags;
    }

    @DynamoDbAttribute("educations")
    public List<String> getEducations() {
        return educations;
    }

    @DynamoDbAttribute("personalUrl")
    public List<String> getPersonalUrl() {
        return personalUrl;
    }

    @DynamoDbAttribute("experiences")
    public List<String> getExperiences() {
        return experiences;
    }
}
