package com.example.planes.dao;

import com.example.planes.entity.enums.PlaneStatus;
import com.example.planes.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface PlaneRepository extends JpaRepository<Plane, UUID>, JpaSpecificationExecutor<Plane> {
    Plane getPlaneById(UUID id);
    List<Plane> findAllByStatus(PlaneStatus planeStatus);
}
