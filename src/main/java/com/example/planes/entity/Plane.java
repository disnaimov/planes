package com.example.planes.entity;

import com.example.planes.entity.enums.PlaneStatus;
import com.example.planes.entity.enums.PlaneType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "planes")
@NoArgsConstructor
@AllArgsConstructor
public class Plane {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;
    @Column(name = "capacity")
    private int capacity;
    @Column(name = "plane_type")
    @Enumerated(EnumType.STRING)
    private PlaneType type;
    @Column(name = "plane_status")
    @Enumerated(EnumType.STRING)
    private PlaneStatus status;
    @Column(name = "technical_date")
    private LocalDateTime technicalDate;
}
