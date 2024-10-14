package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Application {

    private String pk;   // USER#<UserID>
    private String sk;   // APPLICATION#<FeedID>
    private String entityType;  // "Application"
    private String userID; 
    private String feedID;
    private String part;  // 지원 분야
    private String status;  // 지원서 상태 (Pending, Accepted, Rejected)
    private LocalDateTime timestamp;  // 신청 시간
}
