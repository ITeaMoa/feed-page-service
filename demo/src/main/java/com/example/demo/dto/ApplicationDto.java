package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.demo.constant.StatusType;


//dto만듬 보기쉽다
public class ApplicationDto {
    private String userId;   
    private String feedId;   
    private String part;
    private StatusType status;
    private LocalDateTime applicationTimestamp;

    private String creatorId;
    private String title;
    private String content;

    private List<String> tags;
    private Integer recruitmentNum;
    private LocalDateTime deadline;

    private Integer period;
    private Integer likesCount;
    private Map<String, Integer> recruitmentRoles;  

    private String nickname;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getRecruitmentNum() {
        return recruitmentNum;
    }

    public void setRecruitmentNum(Integer recruitmentNum) {
        this.recruitmentNum = recruitmentNum;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Map<String, Integer> getRecruitmentRoles() {
        return recruitmentRoles;
    }

    public void setRecruitmentRoles(Map<String, Integer> recruitmentRoles) {
        this.recruitmentRoles = recruitmentRoles;
    }



    public String getNickname() {
        return nickname;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public StatusType getStatus() {
        return status;
    }
    
    public void setStatus(StatusType status) {
        this.status = status;
    }

    public LocalDateTime getApplicationTimestamp() {
        return applicationTimestamp;
    }

    public void setApplicationTimestamp(LocalDateTime applicationTimestamp) {
        this.applicationTimestamp = applicationTimestamp;
    }
    
   
}
