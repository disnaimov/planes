package com.example.planes.service;

import com.example.planes.dto.ActionResponseDto;
import com.example.planes.dto.PlaneCreateDto;
import com.example.planes.dto.PlaneResponseDto;
import com.example.planes.enums.SortDirection;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface PlaneService {
    void init();

    UUID create(PlaneCreateDto createDto);

    List<PlaneResponseDto> getAll(PageRequest pageRequest);

    List<PlaneResponseDto> filterPlanes(Integer capacity, String type, String status);

    String registerPlane(UUID id);

    void delete(UUID id);

    void technicalService();

    List<ActionResponseDto> getPlaneActions(UUID id, PageRequest pageRequest, SortDirection sortDirection);
}
