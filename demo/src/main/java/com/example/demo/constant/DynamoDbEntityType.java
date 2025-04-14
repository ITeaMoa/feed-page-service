package com.example.demo.constant;

import lombok.Getter;

@Getter
public enum DynamoDbEntityType {
    USER("USER"),
    FEED("FEED"),
    INFO("INFO"),
    PROFILE("PROFILE"),
    FEEDTYPE("FEEDTYPE"),
    MESSAGE("MESSAGE"),
    NOTIFICATION("NOTIFICATION"),
    APPLICATION("APPLICATION"),
    LIKE("LIKE"),
    SAVEDFEED("SAVEDFEED"),
    REPLY("REPLY"),
    COMMENT("COMMENT");
    


    private final String type;

    DynamoDbEntityType(String type) {
        this.type = type;
    }

    // String 값을 DynamoDbEntityType으로 변환하는 함ㅁ수
    public static DynamoDbEntityType fromString(String type) {
        for (DynamoDbEntityType entityType : DynamoDbEntityType.values()) {
            if (entityType.getType().equalsIgnoreCase(type)) {
                return entityType;
            }
        }
        throw new IllegalArgumentException("유효하지않은 엔티티 타입: " + type);
    }
}
