package com.techblog.backend.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TSVectorConverter implements AttributeConverter<String, Object> {

    @Override
    public Object convertToDatabaseColumn(String attribute) {
        return attribute; // PostgreSQL will handle the cast
    }

    @Override
    public String convertToEntityAttribute(Object dbData) {
        if (dbData == null) return null;
        return dbData.toString();
    }
}