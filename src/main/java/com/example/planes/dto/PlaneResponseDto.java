package com.example.planes.dto;

import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaneResponseDto {
    private UUID id;
    private int capacity;
    private PlaneType type;
    private PlaneStatus status;
    @JsonProperty("technical_date")
    private LocalDateTime technicalDate;
}
