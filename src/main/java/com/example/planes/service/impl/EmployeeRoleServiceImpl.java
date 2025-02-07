package com.example.planes.service.impl;

import com.example.planes.dao.EmployeeRoleRepository;
import com.example.planes.model.EmployeeRole;
import com.example.planes.service.EmployeeRoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeRoleServiceImpl implements EmployeeRoleService {
    private final EmployeeRoleRepository employeeRoleRepository;

    @Override
    @PostConstruct
    // заполнение employee_roles тестовыми данными
    public void init() {
        if (employeeRoleRepository.findAll().isEmpty()) {
            List<String> roles = List.of("Pilot", "Stewardess", "Stewardess", "Engineer", "Security", "Engineer", "Cleaner", "Cleaner", "Pilot", "Security");
            List<EmployeeRole> employeeRoles = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                EmployeeRole role = new EmployeeRole();
                role.setRole(roles.get(i));

                employeeRoles.add(role);
            }

            employeeRoleRepository.saveAll(employeeRoles);
        }
    }
}
