package com.example.planes.dao;

import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.example.planes.model.Plane;
import org.springframework.data.domain.PageRequest;
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
