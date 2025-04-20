package com.example.planes.service.impl;

import com.example.planes.dao.ActionRepository;
import com.example.planes.dao.PlaneRepository;
import com.example.planes.dao.ProducerRepository;
import com.example.planes.dto.ActionResponseDto;
import com.example.planes.dto.PlaneCreateDto;
import com.example.planes.dto.PlaneResponseDto;
import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.example.planes.enums.SortDirection;
import com.example.planes.entity.Plane;
import com.example.planes.entity.enums.PlaneStatus;
import com.example.planes.entity.enums.PlaneType;
import com.example.planes.exception.InvalidEntityDataException;
import com.example.planes.filter.specification.PlaneSpecifications;
import com.example.planes.model.Action;
import com.example.planes.model.Plane;
import com.example.planes.model.Producer;
import com.example.planes.service.PlaneService;
import com.example.planes.service.model.PlaneCreateModel;
import com.example.planes.service.model.PlaneDeleteResponseModel;
import com.example.planes.service.model.PlaneRegisterResponseModel;
import com.example.planes.service.model.PlaneResponseModel;
import com.example.planes.service.model.PlaneTOResponseModel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaneServiceImpl implements PlaneService {
    // заполняет plane_service_points случайными комбинациями
    private static final String SHUFFLE_INSERT =
            "INSERT INTO plane_service_points (plane_id, service_point_id) " +
                    "SELECT p.id, sp.id " +
                    "FROM planes p " +
                    "CROSS JOIN service_points sp " +
                    "WHERE RANDOM() < 0.5 LIMIT 10";
    private final PlaneRepository planeRepository;
    private final ProducerRepository producerRepository;
    private final ActionRepository actionRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @PostConstruct
    // заполнение planes тестовыми данными
    public void init() {
        if (planeRepository.findAll().isEmpty()) {
            Random random = new Random();
            int minCapacity = 100000;
            int maxCapacity = 1500000;
            List<Plane> planes = new ArrayList<>();
            List<Producer> producers = producerRepository.findAll();
            List<PlaneType> planeTypes = List.of(PlaneType.CARGO,
                    PlaneType.CARGO,
                    PlaneType.PASSENGER,
                    PlaneType.PASSENGER,
                    PlaneType.CARGO,
                    PlaneType.PASSENGER,
                    PlaneType.CARGO,
                    PlaneType.CARGO,
                    PlaneType.PASSENGER,
                    PlaneType.CARGO);

            List<PlaneStatus> planeStatuses = List.of(PlaneStatus.SERVICE,
                    PlaneStatus.SERVICE,
                    PlaneStatus.FLIGHT,
                    PlaneStatus.FLIGHT,
                    PlaneStatus.WAITING_SERVICE,
                    PlaneStatus.SERVICE,
                    PlaneStatus.WAITING_SERVICE,
                    PlaneStatus.SERVICE,
                    PlaneStatus.FLIGHT,
                    PlaneStatus.FLIGHT);

            List<LocalDateTime> technicalDates = List.of(LocalDateTime.of(2021, 3, 15, 10, 30),
                    LocalDateTime.of(2022, 7, 22, 14, 45),
                    LocalDateTime.of(2023, 1, 5, 8, 15),
                    LocalDateTime.of(2024, 9, 30, 19, 0),
                    LocalDateTime.of(2025, 11, 11, 23, 59, 59),
                    LocalDateTime.of(2020, 12, 25, 15, 0),
                    LocalDateTime.of(2023, 4, 1, 12, 30),
                    LocalDateTime.of(2022, 10, 10, 9, 0),
                    LocalDateTime.of(2025, 5, 5, 18, 45),
                    LocalDateTime.of(2024, 6, 18, 7, 20));
            for (int i = 0; i < 10; i++) {
                Plane plane = new Plane();
                plane.setType(planeTypes.get(i));
                plane.setStatus(planeStatuses.get(i));
                plane.setCapacity(random.nextInt(maxCapacity - minCapacity + 1) + minCapacity);
                plane.setTechnicalDate(technicalDates.get(i));
                plane.setProducer(producers.get(i));

                planes.add(plane);
            }

            planeRepository.saveAll(planes);

            // проверим что plane_service_points пуста
            Integer num = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plane_service_points", Integer.class);
            if (num == null || num == 0) {
                jdbcTemplate.update(SHUFFLE_INSERT);
            }
        }
    }

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
            throw new InvalidEntityDataException("Passed id does not exist", "INCORRECT_ID", HttpStatus.NOT_FOUND);
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

    @Override
    public List<ActionResponseDto> getPlaneActions(UUID id, PageRequest pageRequest, SortDirection sortDirection) {
        List<ActionResponseDto> actionResponseDtos = new ArrayList<>();
        log.info("Fetching actions for plane with ID: {}, sort direction: {}", id, sortDirection);
        Page<Action> planeActions = actionRepository.getActionsByPlane(planeRepository.getPlaneById(id), pageRequest);
        log.info("Fetched {} actions for plane ID: {}", planeActions.getTotalElements(), id);

        for (Action currentAction : planeActions) {
            ActionResponseDto actionResponseDto = getActionResponseDto(currentAction);

            log.debug("Processed action: {}", currentAction);
            actionResponseDtos.add(actionResponseDto);
        }


        // сортируем по переданному порядку
        if (sortDirection.equals(SortDirection.ASC)) {
            log.info("Sorting actions in ASC order");
            actionResponseDtos = actionResponseDtos.stream()
                    .sorted(Comparator.comparing(ActionResponseDto::getActionDate))
                    .toList();
        } else if (sortDirection.equals(SortDirection.DESC)) {
            log.info("Sorting actions in DESC order");
            actionResponseDtos = actionResponseDtos.stream()
                    .sorted(Comparator.comparing(ActionResponseDto::getActionDate).reversed())
                    .toList();
        }

        log.info("Returning {} sorted action responses", actionResponseDtos.size());
        return actionResponseDtos;
    }

    // заполняет дтошку данными из переданного action
    private static ActionResponseDto getActionResponseDto(Action currentAction) {
        ActionResponseDto actionResponseDto = new ActionResponseDto();

        actionResponseDto.setId(currentAction.getId());
        actionResponseDto.setAction(currentAction.getAction());
        actionResponseDto.setActionDate(currentAction.getActionDate());
        actionResponseDto.setPlane_id(currentAction.getPlane().getId());
        actionResponseDto.setService_point_id(currentAction.getServicePoint().getId());
        actionResponseDto.setEmployee_id(currentAction.getEmployee().getId());
        return actionResponseDto;
    }
}
