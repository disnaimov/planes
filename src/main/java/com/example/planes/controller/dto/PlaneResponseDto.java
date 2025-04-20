package com.example.planes.controller.dto;

import com.example.planes.entity.enums.PlaneStatus;
import com.example.planes.entity.enums.PlaneType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private PlaneType type;
    private PlaneStatus status;
    @JsonProperty("technical_date")
    private LocalDateTime technicalDate;
}
