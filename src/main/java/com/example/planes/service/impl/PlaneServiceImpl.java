package com.example.planes.service.impl;

import com.example.planes.dao.PlaneRepository;
import com.example.planes.dto.PlaneCreateDto;
import com.example.planes.dto.PlaneFilterDto;
import com.example.planes.dto.PlaneResponseDto;
import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.example.planes.exception.InvalidEntityDataException;
import com.example.planes.filter.specification.PlaneSpecifications;
import com.example.planes.model.Plane;
import com.example.planes.service.PlaneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaneServiceImpl implements PlaneService {
    private final PlaneRepository planeRepository;

    @Override
    public UUID create(PlaneCreateDto createDto) {
        Plane plane = new Plane();
        plane.setCapacity(createDto.getCapacity());

        PlaneType type = PlaneType.fromString(createDto.getType());
        PlaneStatus status = PlaneStatus.fromString(createDto.getStatus());

        plane.setType(type);
        plane.setStatus(status);
        plane.setTechnicalDate(createDto.getTechnicalDate());

        planeRepository.save(plane);

        return plane.getId();
    }

    @Override
    public List<PlaneResponseDto> getAll(PageRequest pageRequest) {
        List<Plane> planes = planeRepository.findAll(pageRequest).getContent();
        return getPlaneResponseDtos(planes);
    }

    private List<PlaneResponseDto> getPlaneResponseDtos(List<Plane> planes) {
        List<PlaneResponseDto> planeResponseDtos = new ArrayList<>();

        planes.stream()
                .forEach(plane -> {
                    PlaneResponseDto responseDto = new PlaneResponseDto();
                    responseDto.setId(plane.getId());
                    responseDto.setCapacity(plane.getCapacity());
                    responseDto.setType(plane.getType());
                    responseDto.setStatus(plane.getStatus());
                    responseDto.setTechnicalDate(plane.getTechnicalDate());

                    planeResponseDtos.add(responseDto);
                });

        return planeResponseDtos;
    }

    @Override
    public List<PlaneResponseDto> filterPlanes(PlaneFilterDto planeFilterDto) {
        Specification<Plane> spec = Specification.where(PlaneSpecifications.filterByCapacity(planeFilterDto.getCapacity()))
                .and(PlaneSpecifications.filterByType(planeFilterDto.getPlaneType()))
                .and(PlaneSpecifications.filterByStatus(planeFilterDto.getPlaneStatus()));

        List<Plane> planes = planeRepository.findAll(spec);
        return getPlaneResponseDtos(planes);
    }

    @Override
    public String registerPlane(UUID id) {
        Plane plane = planeRepository.getPlaneById(id);

        if (plane != null) {
            if (plane.getStatus() == PlaneStatus.FLIGHT) { // проверить статус
                plane.setCapacity(0);
                plane.setStatus(PlaneStatus.WAITING_SERVICE); // изменяем статус на ожидает
                planeRepository.save(plane);
            } else {
                throw new IllegalStateException("Plane must be in flight to be registered!"); // исключение
            }
        } else {
            throw new NoSuchElementException("Plane with id " + id + " does not exist."); // исключение, если самолет не найден
        }
        return "Plane with id " + id + " was successfulnesses registered";
    }

    @Override
    public void delete(UUID id) {
        log.info("Removal plane by id");
        log.debug("Removal plane by id {}", id);
        if (planeRepository.findById(id).isPresent()) {
            planeRepository.delete(planeRepository.findById(id).orElseThrow());
        }
        else throw new InvalidEntityDataException("Ошибка: указанный id не существует", "INCORRECT_ID", HttpStatus.NOT_FOUND);
        log.info("Plane by id removed");
        log.debug("Plane by id removed {}", planeRepository.findById(id));
    }

    @Override
    public void technicalService() {
        List<Plane> planes = planeRepository.findAllByStatus(PlaneStatus.SERVICE);
        planes.stream()
                .forEach(plane -> {
                    plane.setStatus(PlaneStatus.WAITING_SERVICE);
                    plane.setTechnicalDate(LocalDateTime.now());
                    planeRepository.save(plane);
                });
    }
}
