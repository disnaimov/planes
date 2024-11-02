package com.example.planes.converter;

import com.example.planes.enums.PlaneStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PlaneStatusConverter implements AttributeConverter<PlaneStatus, String> {

    @Override
    public String convertToDatabaseColumn(PlaneStatus attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public PlaneStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? PlaneStatus.fromString(dbData) : null;
    }
}
