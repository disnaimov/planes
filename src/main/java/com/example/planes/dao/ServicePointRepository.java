package com.example.planes.dao;

import com.example.planes.entity.ServicePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServicePointRepository extends JpaRepository<ServicePoint, UUID> {
}
