package com.example.planes.service.impl;

import com.example.planes.dao.ActionRepository;
import com.example.planes.dao.EmployeeRepository;
import com.example.planes.dao.PlaneRepository;
import com.example.planes.dao.ServicePointRepository;
import com.example.planes.entity.Action;
import com.example.planes.entity.Employee;
import com.example.planes.entity.Plane;
import com.example.planes.entity.ServicePoint;
import com.example.planes.enums.PlaneAction;
import com.example.planes.service.ActionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@DependsOn("employeeServiceImpl")
public class ActionServiceImpl implements ActionService {
    private final PlaneRepository planeRepository;
    private final ActionRepository actionRepository;
    private final ServicePointRepository servicePointRepository;
    private final EmployeeRepository employeeRepository;

    private static final List<LocalDateTime> dates = List.of(
            LocalDateTime.of(2023, 1, 10, 10, 0),
            LocalDateTime.of(2023, 2, 15, 12, 30),
            LocalDateTime.of(2023, 3, 20, 14, 45),
            LocalDateTime.of(2023, 4, 25, 16, 0),
            LocalDateTime.of(2023, 5, 30, 18, 15),
            LocalDateTime.of(2023, 6, 5, 9, 0),
            LocalDateTime.of(2023, 7, 10, 11, 30),
            LocalDateTime.of(2023, 8, 15, 13, 45),
            LocalDateTime.of(2023, 9, 20, 15, 0),
            LocalDateTime.of(2023, 10, 25, 17, 30));

    private static final List<String> planeActions = List.of(
            "arrival",
            "arrival",
            "maintenance",
            "departure",
            "arrival",
            "maintenance",
            "maintenance",
            "departure",
            "departure",
            "maintenance"
    );

    @Override
    @PostConstruct
    public void init() {
        if (actionRepository.findAll().isEmpty()) {
            List<Action> actions = new ArrayList<>();
            List<Plane> planes = planeRepository.findAll();
            List<ServicePoint> servicePoints = servicePointRepository.findAll();
            List<Employee> employees = employeeRepository.findAll();

            for (int i = 0; i < 10; i++) {
                Action action = getAction(i, planes, servicePoints, employees);

                actions.add(action);
            }

            actionRepository.saveAll(actions);
        }
    }

    private Action getAction(int i, List<Plane> planes, List<ServicePoint> servicePoints, List<Employee> employees) {
        Action action = new Action();
        action.setAction(PlaneAction.fromString(planeActions.get(i)));
        action.setActionDate(dates.get(i));
        action.setPlane(planes.get(i));
        action.setServicePoint(servicePoints.get(i));
        action.setEmployee(employees.get(i));

        return action;
    }
}
