package com.example.planes.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlaneAction {
    ARRIVAL("arrival"),
    DEPARTURE("departure"),
    MAINTENANCE("maintenance");

    private final String value;

    PlaneAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PlaneAction fromString(String value) {
        for (PlaneAction item : PlaneAction.values()) {
            if (item.getValue().equalsIgnoreCase(value)) {
                return item;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid " + PlaneAction.class.getSimpleName());
    }
}
