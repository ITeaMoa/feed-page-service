package com.example.demo.entity;


import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import com.example.demo.constant.DynamoDbEntityType;
import com.example.demo.converter.LocalDateTimeConverter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class Comment extends BaseEntity {

    private String commentId; // 댓글 고유 식별자
    private String userId;
    private String comment;
    private String nickname;

    @DynamoDbAttribute("entityType")
    public DynamoDbEntityType getEntityType() {
        return DynamoDbEntityType.COMMENT;
    }

    @DynamoDbAttribute("commentId")
    public String getCommentId() {
        return commentId;
    }

    @DynamoDbAttribute("userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("comment")
    public String getComment() {
        return comment;
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
}
