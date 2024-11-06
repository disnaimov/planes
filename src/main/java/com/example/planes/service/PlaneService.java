package com.example.planes.service;

import com.example.planes.dto.PlaneCreateDto;
import com.example.planes.dto.PlaneFilterDto;
import com.example.planes.dto.PlaneResponseDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;
/*
 4. Пользователь должен иметь возможность запустить процесс техобслуживания
 свободных самолетов.
 */
public interface PlaneService {
    UUID create(PlaneCreateDto createDto);
    List<PlaneResponseDto> getAll(PageRequest pageRequest);
    List<PlaneResponseDto> filterPlanes(Integer capacity, String type, String status);
    String registerPlane(UUID id);
    void delete(UUID id);
    void technicalService();
}
