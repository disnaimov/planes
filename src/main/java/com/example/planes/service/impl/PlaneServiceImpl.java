package com.example.planes.service.impl;

import com.example.planes.dao.PlaneRepository;
import com.example.planes.dto.PlaneCreateDto;
import com.example.planes.dto.PlaneResponseDto;
import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.example.planes.exception.InvalidEntityDataException;
import com.example.planes.filter.specification.PlaneSpecifications;
import com.example.planes.model.Plane;
import com.example.planes.scheduling.TransactionalCapacityUpdateJob;
import com.example.planes.service.PlaneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaneServiceImpl implements PlaneService {
    private final PlaneRepository planeRepository;
    private final TransactionTemplate transactionTemplate;
    private final TransactionalCapacityUpdateJob job;

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
            throw new InvalidEntityDataException("Ошибка: указанный id не существует", "INCORRECT_ID", HttpStatus.NOT_FOUND);
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transactionUpdate() {
        job.schedule();
    }

}
