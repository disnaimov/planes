package com.example.planes.dao;

import com.example.planes.entity.Action;
import com.example.planes.entity.Plane;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActionRepository extends JpaRepository<Action, UUID> {
    Page<Action> getActionsByPlane(Plane plane, PageRequest pageRequest);
}
