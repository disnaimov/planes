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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @ManyToOne
    @JoinColumn(name = "producer_id")
    private Producer producer;
    @ManyToMany
    @JoinTable(
            name = "plane_service_points",
            joinColumns = @JoinColumn(name = "plane_id"),
            inverseJoinColumns = @JoinColumn(name = "service_point_id")
    )
    private List<ServicePoint> servicePoints = new ArrayList<>();
}
