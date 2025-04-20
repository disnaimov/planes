package com.example.planes.dao;

import com.example.planes.entity.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, UUID> {
}
