package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Application {

    private String pk;   // USER#<UserID>
    private String sk;   // APPLICATION#<FeedID>
    private String entityType = "Application";  // "Application" 설정

    private String part;  // 지원 분야
    private String status;  // 지원서 상태 (Pending, Accepted, Rejected)
    private LocalDateTime timestamp;  // 신청 시간

    private String name;

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    @DynamoDbAttribute("Part")
    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    @DynamoDbAttribute("Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("Timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}



