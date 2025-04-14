package com.example.demo.converter;

import java.time.LocalDateTime;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime> {

    @Override
    public AttributeValue transformFrom(LocalDateTime input) {
        return input != null ? AttributeValue.builder().s(input.toString()).build() : null;
    }

    @Override
    public LocalDateTime transformTo(AttributeValue attributeValue) {
        return attributeValue != null && attributeValue.s() != null
                ? LocalDateTime.parse(attributeValue.s())
                : null;
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
