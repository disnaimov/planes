package com.example.planes.dto;

import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaneFilterDto {
    private Integer capacity;
    @JsonProperty("type")
    private PlaneType planeType;
    @JsonProperty("status")
    private PlaneStatus planeStatus;
}
