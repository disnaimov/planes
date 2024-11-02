package com.example.planes.model;

import com.example.planes.converter.PlaneStatusConverter;
import com.example.planes.converter.PlaneTypeConverter;
import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
    @Convert(converter = PlaneTypeConverter.class)
    private PlaneType type;
    @Column(name = "plane_status")
    @Convert(converter = PlaneStatusConverter.class)
    private PlaneStatus status;
    @Column(name = "technical_date")
    private LocalDateTime technicalDate;
}
