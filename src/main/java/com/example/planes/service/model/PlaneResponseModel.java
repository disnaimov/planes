package com.example.planes.service.model;

import com.example.planes.entity.enums.PlaneStatus;
import com.example.planes.entity.enums.PlaneType;
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
@NoArgsConstructor
@AllArgsConstructor
public class PlaneResponseModel {
    private UUID id;
    private int capacity;
    @Enumerated(EnumType.STRING)
    private PlaneType type;
    private PlaneStatus status;
    private LocalDateTime technicalDate;
}
