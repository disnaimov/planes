package com.example.planes.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlaneType {
    CARGO("cargo"),
    PASSENGER("passenger");

    private final String value;

    PlaneType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PlaneType fromString(String value) {
        for (PlaneType item : PlaneType.values()) {
            if (item.getValue().equalsIgnoreCase(value)) {
                return item;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid " + PlaneType.class.getSimpleName());
    }
}