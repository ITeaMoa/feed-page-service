package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.demo.constant.DynamoDbEntityType;
import com.example.demo.converter.LocalDateTimeConverter;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class FeedEntity extends BaseEntity {

    private String title;
    private String content;
    private Boolean postStatus;
    private Boolean savedFeed;
    private List<String> tags;
    private List<Comment> comments;
    private Integer recruitmentNum;
    private LocalDateTime deadline;
    private String place;
    private Integer period;
    private Integer likesCount;
    private Map<String, Integer> recruitmentRoles;
    private Map<String, Integer> roles;
    private String nickname;
    private String imageUrl;

    @DynamoDbAttribute("entityType")
    public DynamoDbEntityType getEntityType() {
        return DynamoDbEntityType.FEED;
    }

    // 이하 기타 필드 매핑
    @DynamoDbAttribute("title")
    public String getTitle() {
        return title;
    }

    @DynamoDbAttribute("content")
    public String getContent() {
        return content;
    }

    @DynamoDbAttribute("postStatus")
    public Boolean getPostStatus() {
        return postStatus;
    }

    @DynamoDbAttribute("savedFeed")
    public Boolean getSavedFeed() {
        return savedFeed;
    }

    @DynamoDbAttribute("tags")
    public List<String> getTags() {
        return tags;
    }

    @DynamoDbAttribute("comments")
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @DynamoDbAttribute("recruitmentNum")
    public Integer getRecruitmentNum() {
        return recruitmentNum;
    }

    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @DynamoDbAttribute("deadline")
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @DynamoDbAttribute("place")
    public String getPlace() {
        return place;
    }

    @DynamoDbAttribute("period")
    public Integer getPeriod() {
        return period;
    }

    @DynamoDbSecondarySortKey(indexNames = "MostLikedFeed-index")
    @DynamoDbAttribute("likesCount")
    public Integer getLikesCount() {
        return likesCount;
    }

    @DynamoDbAttribute("roles")
    public Map<String, Integer> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Integer> roles) {
        this.roles = roles;
    }

    @DynamoDbAttribute("recruitmentRoles")
    public Map<String, Integer> getRecruitmentRoles() {
        return recruitmentRoles;
    }

    public void setRecruitmentRoles(Map<String, Integer> recruitmentRoles) {
        this.recruitmentRoles = recruitmentRoles;
    }

    @DynamoDbAttribute("nickname")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @DynamoDbAttribute("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
