package com.example.planes.service.impl;

import com.example.planes.dao.ActionRepository;
import com.example.planes.dao.PlaneRepository;
import com.example.planes.dao.ProducerRepository;
import com.example.planes.dao.ServicePointRepository;
import com.example.planes.dto.ActionResponseDto;
import com.example.planes.dto.PlaneCreateDto;
import com.example.planes.dto.PlaneResponseDto;
import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.example.planes.enums.SortDirection;
import com.example.planes.exception.InvalidEntityDataException;
import com.example.planes.filter.specification.PlaneSpecifications;
import com.example.planes.model.Action;
import com.example.planes.model.Plane;
import com.example.planes.model.Producer;
import com.example.planes.model.ServicePoint;
import com.example.planes.service.PlaneService;
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
    private final PlaneRepository planeRepository;
    private final ProducerRepository producerRepository;
    private final ActionRepository actionRepository;
    private final ServicePointRepository servicePointRepository;
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
                List<Plane> allPlanes = planeRepository.findAll();
                List<ServicePoint> allServicePoints = servicePointRepository.findAll();

                // заполним plane_service_points
                for (int i = 0; i < 10; i++) {
                    Plane plane = allPlanes.get(i);
                    ServicePoint servicePoint = allServicePoints.get(i);

                    jdbcTemplate.update("INSERT INTO plane_service_points (plane_id, service_point_id) VALUES (?, ?)",
                            plane.getId(), servicePoint.getId());
                }
            }
        }
    }

    @Override
    public UUID create(PlaneCreateDto createDto) {
        // создаем новый самолет и заполняем данным из дто
        log.info("Creating a new plane with details: {}", createDto);
        Plane plane = new Plane();
        plane.setCapacity(createDto.getCapacity());
        plane.setType(PlaneType.fromString(createDto.getType()));
        plane.setStatus(PlaneStatus.fromString(createDto.getStatus()));
        plane.setTechnicalDate(createDto.getTechnicalDate());

        planeRepository.save(plane); // сохраняем
        log.info("New plane created with ID: {}", plane.getId());

        return plane.getId();
    }

    @Override
    public List<PlaneResponseDto> getAll(PageRequest pageRequest) {
        // собираем список всех самолетов с учетом паггинации
        log.info("Fetching all planes with pagination: {}", pageRequest);
        List<Plane> planes = planeRepository.findAll(pageRequest).getContent();
        log.info("Retrieved {} planes.", planes.size());
        return getPlaneResponseDtos(planes);
    }

    // маппит список самолетов в список респонс дтошек
    private List<PlaneResponseDto> getPlaneResponseDtos(List<Plane> planes) {
        log.info("Mapping {} planes to response DTOs.", planes.size());
        List<PlaneResponseDto> planeResponseDtos = new ArrayList<>();

        planes.stream()
                .forEach(plane -> {
                    // создаем и заполняем дтошку
                    PlaneResponseDto responseDto = new PlaneResponseDto();
                    responseDto.setId(plane.getId());
                    responseDto.setCapacity(plane.getCapacity());
                    responseDto.setType(plane.getType());
                    responseDto.setStatus(plane.getStatus());
                    responseDto.setTechnicalDate(plane.getTechnicalDate());

                    // добавляем в список
                    planeResponseDtos.add(responseDto);
                });

        return planeResponseDtos;
    }

    @Override
    public List<PlaneResponseDto> filterPlanes(Integer capacity, String type, String status) {
        // собираем спку по переданным фильтрам
        log.info("Filtering planes with capacity: {}, type: {}, status: {}", capacity, type, status);
        Specification<Plane> spec = Specification.where(PlaneSpecifications.filterByCapacity(capacity))
                .and(PlaneSpecifications.filterByType(type))
                .and(PlaneSpecifications.filterByStatus(status));

        // получаем и возвращаем список с учетом спеки
        List<Plane> planes = planeRepository.findAll(spec);
        log.info("Filtered {} planes based on criteria.", planes.size());
        return getPlaneResponseDtos(planes);
    }

    @Override
    public String registerPlane(UUID id) {
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
        return "Plane with id " + id + " was successfulnesses registered";
    }

    @Override
    public void delete(UUID id) {
        log.info("Removal plane by id {}", id);
        if (planeRepository.findById(id).isPresent()) {
            planeRepository.delete(planeRepository.findById(id).orElseThrow());
        } else
            throw new InvalidEntityDataException("Passed id does not exist", "INCORRECT_ID", HttpStatus.NOT_FOUND);
        log.info("Plane by id removed {}", planeRepository.findById(id));
    }

    @Override
    public void technicalService() {
        // собираем всех кто в сервисе
        log.info("Starting technical service for planes in SERVICE status.");
        List<Plane> planes = planeRepository.findAllByStatus(PlaneStatus.SERVICE);
        planes.stream()
                .forEach(plane -> {
                    // проводим обслуживание, меняем статус и время, сохраняем
                    log.info("Processing plane with ID: {}", plane.getId());
                    plane.setStatus(PlaneStatus.WAITING_SERVICE);
                    plane.setTechnicalDate(LocalDateTime.now());
                    planeRepository.save(plane);
                    log.info("Plane with ID: {} status updated to WAITING_SERVICE.", plane.getId());
                });
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
