package com.example.planes.dto;

import com.example.planes.enums.PlaneAction;
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
public class ActionResponseDto {
    private UUID id;
    private PlaneAction action;
    private LocalDateTime actionDate;
    private UUID plane_id;
    private UUID service_point_id;
    private UUID employee_id;
}
