package com.example.planes.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaneCreateModel {
    private int capacity;
    private String type;
    private String status;
    private LocalDateTime technicalDate;
}
