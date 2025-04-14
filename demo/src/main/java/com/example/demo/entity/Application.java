package com.example.demo.entity;

import com.example.demo.constant.DynamoDbEntityType;
import com.example.demo.constant.StatusType;
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
public class Application extends BaseEntity {

    
    private String part; // 지원 분야
    private StatusType status; // 상태 Enum 사용
     
    @DynamoDbAttribute("entityType")
    public DynamoDbEntityType getEntityType() {
        return DynamoDbEntityType.APPLICATION;
    }
    
    @DynamoDbAttribute("part")
    public String getPart() {
        return part;
    }

    @DynamoDbAttribute("status")
    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    //  BaseEntity에서 상속
    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    @Override
    @DynamoDbAttribute("timestamp")
    public LocalDateTime getTimestamp() {
        return super.getTimestamp();
    }

    @Override
    @DynamoDbPartitionKey  //-
    @DynamoDbAttribute("Pk")
    public String getPk() {
        return super.getPk();
    }

    @Override
    @DynamoDbSortKey  // -
    @DynamoDbAttribute("Sk")
    public String getSk() {
        return super.getSk();
    }

    @Override
    @DynamoDbAttribute("creatorId")
    public String getCreatorId() {
        return super.getCreatorId();
    }

    @Override
    @DynamoDbAttribute("userStatus")
    public Boolean getUserStatus() {
        return super.getUserStatus();
    }
}
