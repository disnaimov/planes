package com.example.planes.service.impl;

import com.example.planes.dao.EmployeeRepository;
import com.example.planes.dao.EmployeeRoleRepository;
import com.example.planes.model.Employee;
import com.example.planes.model.EmployeeRole;
import com.example.planes.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@DependsOn("employeeRoleServiceImpl")
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRoleRepository employeeRoleRepository;

    private static final List<String> names = List.of("Emily Wilson",
            "Michael Davis",
            "Sophia Lee",
            "William Brown",
            "Olivia Taylor",
            "James Martin",
            "Ava White",
            "Robert Jenkins",
            "Isabella Garcia",
            "Ethan Hall");
    private static final List<String> phones = List.of("+1 (202) 123-4567",
            "+1 (555) 901-2345",
            "+1 (312) 789-0123",
            "+1 (415) 567-8901",
            "+1 (212) 345-6789",
            "+1 (617) 901-2345",
            "+1 (303) 123-4567",
            "+1 (404) 567-8901",
            "+1 (510) 789-0123",
            "+1 (713) 345-6789");
    private static final List<String> addresses = List.of("123 Main Street, New York, NY 10001",
            "456 Elm Street, Los Angeles, CA 90001",
            "789 Oak Street, Chicago, IL 60601",
            "901 Maple Street, Houston, TX 77001",
            "234 Pine Street, Phoenix, AZ 85001",
            "567 Cedar Street, Philadelphia, PA 19101",
            "890 Walnut Street, San Antonio, TX 78201",
            "345 Spruce Street, San Diego, CA 92101",
            "678 Fir Street, Dallas, TX 75201",
            "901 Ash Street, San Jose, CA 95101");

    @Override
    @PostConstruct
    // заполнение employees тестовыми данными
    public void init() {
        if (employeeRepository.findAll().isEmpty()) {

            List<Employee> employees = new ArrayList<>();
            List<EmployeeRole> roles = employeeRoleRepository.findAll();

            // создание 10 сотрудников
            for (int i = 0; i < 10; i++) {
                Employee employee = getEmployee(i, roles);

                employees.add(employee);
            }

            employeeRepository.saveAll(employees);
        }
    }

    // создание и заполнение employee тестовыми данными из тестовых списков по индексу
    private static Employee getEmployee(int i, List<EmployeeRole> employeeRoles) {
        Employee employee = new Employee();
        employee.setName(names.get(i));
        employee.setAddress(addresses.get(i));
        employee.setPhone(phones.get(i));
        employee.setRole(employeeRoles.get(i));

        return employee;
    }
}
