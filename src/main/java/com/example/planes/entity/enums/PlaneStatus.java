package com.example.planes.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlaneStatus {
    FLIGHT("flight"),
    SERVICE("service"),
    WAITING_SERVICE("waiting_service");

    private final String value;

    PlaneStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PlaneStatus fromString(String value) {
        for (PlaneStatus item : PlaneStatus.values()) {
            if (item.getValue().equalsIgnoreCase(value)) {
                return item;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid " + PlaneType.class.getSimpleName());
    }
}
