package com.example.planes.service.impl;

import com.example.planes.dao.PlaneRepository;
import com.example.planes.entity.Plane;
import com.example.planes.entity.enums.PlaneStatus;
import com.example.planes.entity.enums.PlaneType;
import com.example.planes.exception.InvalidEntityDataException;
import com.example.planes.service.PlaneService;
import com.example.planes.service.model.PlaneCreateModel;
import com.example.planes.service.model.PlaneDeleteResponseModel;
import com.example.planes.service.model.PlaneRegisterResponseModel;
import com.example.planes.service.model.PlaneResponseModel;
import com.example.planes.service.model.PlaneTOResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaneServiceImpl implements PlaneService {
    private final PlaneRepository planeRepository;

    @Override
    public UUID create(PlaneCreateModel createModel) {

        // проверка грузоподъемности
        if (createModel.getCapacity() < 0) {
            throw new InvalidEntityDataException("Capacity must be greater than or equal to 0.", "INCORRECT_CAPACITY", HttpStatus.BAD_REQUEST);
        }

        // проверка типа самолета
        PlaneType planeType;
        try {
            planeType = PlaneType.fromString(createModel.getType());
        } catch (IllegalArgumentException e) {
            throw new InvalidEntityDataException("Invalid plane type: " + createModel.getType(), "INCORRECT_TYPE", HttpStatus.BAD_REQUEST);
        }

        // проверка статуса самолета
        PlaneStatus planeStatus;
        try {
            planeStatus = PlaneStatus.fromString(createModel.getStatus());
        } catch (IllegalArgumentException e) {
            throw new InvalidEntityDataException("Invalid plane status: " + createModel.getStatus(), "INCORRECT_STATUS", HttpStatus.BAD_REQUEST);
        }
        // создаем новый самолет и заполняем данным из дто
        log.info("Creating a new plane with details: {}", createModel);
        Plane plane = new Plane();
        plane.setCapacity(createModel.getCapacity());
        plane.setType(PlaneType.fromString(createModel.getType()));
        plane.setStatus(PlaneStatus.fromString(createModel.getStatus()));
        plane.setTechnicalDate(createModel.getTechnicalDate());

        planeRepository.save(plane); // сохраняем
        log.info("New plane created with ID: {}", plane.getId());

        return plane.getId();
    }

    @Override
    public List<PlaneResponseModel> getAll(PageRequest pageRequest) {
        // собираем список всех самолетов с учетом паггинации
        log.info("Fetching all planes with pagination: {}", pageRequest);
        List<Plane> planes = planeRepository.findAll(pageRequest).getContent();
        log.info("Retrieved {} planes.", planes.size());
        return getPlaneResponseModels(planes);
    }

    @Override
    public List<PlaneResponseModel> filterPlanes(Integer capacity, String type, String status) {
        // собираем спку по переданным фильтрам
        log.info("Filtering planes with capacity: {}, type: {}, status: {}", capacity, type, status);
        Specification<Plane> spec = Specification.where(filterByCapacity(capacity))
                .and(filterByType(type))
                .and(filterByStatus(status));

        // получаем и возвращаем список с учетом спеки
        List<Plane> planes = planeRepository.findAll(spec);
        log.info("Filtered {} planes based on criteria.", planes.size());
        return getPlaneResponseModels(planes);
    }

    @Override
    public PlaneRegisterResponseModel registerPlane(UUID id) {
        log.info("Attempting to register plane with ID: {}", id);
        Plane plane = planeRepository.getPlaneById(id);

        if (plane != null) {
            if (plane.getStatus() == PlaneStatus.FLIGHT) { // проверить статус
                log.info("Plane with ID: {} is in FLIGHT status. Updating status.", id);
                plane.setCapacity(0);
                plane.setStatus(PlaneStatus.WAITING_SERVICE); // изменяем статус на ожидает
                planeRepository.save(plane);
                log.info("Plane with ID: {} successfully registered.", id);
            } else {
                log.error("Plane with ID: {} cannot be registered. Current status: {}", id, plane.getStatus());
                throw new InvalidEntityDataException("Plane must be in flight to be registered!", "INCORRECT_STATUS", HttpStatus.BAD_REQUEST); // исключение
            }
        } else {
            log.error("Plane with ID: {} does not exist.", id);
            throw new InvalidEntityDataException("Plane does not exist", "INCORRECT_ID", HttpStatus.NOT_FOUND); // исключение, если самолет не найден
        }
        return new PlaneRegisterResponseModel("Plane successfully registered", id);
    }

    @Override
    public PlaneDeleteResponseModel delete(UUID id) {
        log.info("Removal plane by id {}", id);
        if (planeRepository.findById(id).isPresent()) {
            planeRepository.delete(planeRepository.findById(id).orElseThrow());
        } else
            throw new InvalidEntityDataException("Ошибка: указанный id не существует", "INCORRECT_ID", HttpStatus.NOT_FOUND);
        log.info("Plane by id removed {}", planeRepository.findById(id));

        return new PlaneDeleteResponseModel("Plane successfully deleted", id);
    }

    @Override
    public PlaneTOResponseModel technicalService() {
        // собираем всех кто в сервисе
        log.info("Starting technical service for planes in SERVICE status.");
        List<Plane> planes = planeRepository.findAllByStatus(PlaneStatus.SERVICE);
        List<UUID> ids = new ArrayList<>();
        planes.stream()
                .forEach(plane -> {
                    // проводим обслуживание, меняем статус и время, сохраняем
                    log.info("Processing plane with ID: {}", plane.getId());
                    plane.setStatus(PlaneStatus.WAITING_SERVICE);
                    plane.setTechnicalDate(LocalDateTime.now());
                    planeRepository.save(plane);
                    ids.add(plane.getId());
                    log.info("Plane with ID: {} status updated to WAITING_SERVICE.", plane.getId());
                });

        if (ids.isEmpty()) {
            return new PlaneTOResponseModel("No planes in SERVICE status, no maintenance was done", null);
        }
        return new PlaneTOResponseModel("Maintenance was successful for planes with id", ids);
    }

    // маппит список самолетов в список респонс дтошек
    private List<PlaneResponseModel> getPlaneResponseModels(List<Plane> planes) {
        log.info("Mapping {} planes to response DTOs.", planes.size());
        List<PlaneResponseModel> planeResponseModels = new ArrayList<>();

        planes.stream()
                .forEach(plane -> {
                    // создаем и заполняем дтошку
                    PlaneResponseModel responseModel = new PlaneResponseModel();
                    responseModel.setId(plane.getId());
                    responseModel.setCapacity(plane.getCapacity());
                    responseModel.setType(plane.getType());
                    responseModel.setStatus(plane.getStatus());
                    responseModel.setTechnicalDate(plane.getTechnicalDate());

                    // добавляем в список
                    planeResponseModels.add(responseModel);
                });

        return planeResponseModels;
    }

    private static Specification<Plane> filterByCapacity(Integer capacity) {
        return ((root, query, criteriaBuilder) -> {
            if (capacity == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("capacity"), capacity);
        });
    }

    private static Specification<Plane> filterByType(String type) {
        return ((root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            PlaneType planeType = PlaneType.fromString(type);
            return criteriaBuilder.equal(root.get("type"), planeType);
        });
    }

    private static Specification<Plane> filterByStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            PlaneStatus planeStatus = PlaneStatus.fromString(status);
            return criteriaBuilder.equal(root.get("status"), planeStatus);
        };
    }
}
