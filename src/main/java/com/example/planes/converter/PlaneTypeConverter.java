package com.example.planes.converter;

import com.example.planes.enums.PlaneType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PlaneTypeConverter implements AttributeConverter<PlaneType, String> {
    @Override
    public String convertToDatabaseColumn(PlaneType attribute) {
        return attribute != null ? attribute.getValue() : null;

    }

    @Override
    public PlaneType convertToEntityAttribute(String dbData) {
        return dbData != null ? PlaneType.fromString(dbData) : null;
    }
}
