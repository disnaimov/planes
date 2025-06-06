package com.example.planes.service;

import com.example.planes.entity.enums.SortDirection;
import com.example.planes.service.model.PlaneCreateModel;
import com.example.planes.service.model.PlaneDeleteResponseModel;
import com.example.planes.service.model.PlaneRegisterResponseModel;
import com.example.planes.service.model.PlaneResponseModel;
import com.example.planes.service.model.PlaneTOResponseModel;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;
/*
 4. Пользователь должен иметь возможность запустить процесс техобслуживания
 свободных самолетов.
 */
public interface PlaneService {
    @PostConstruct
    // заполнение planes тестовыми данными
    void init();

    UUID create(PlaneCreateModel createDto);
    List<PlaneResponseModel> getAll(PageRequest pageRequest);
    List<PlaneResponseModel> filterPlanes(Integer capacity, String type, String status);
    PlaneRegisterResponseModel registerPlane(UUID id);
    PlaneDeleteResponseModel delete(UUID id);
    PlaneTOResponseModel technicalService();

    List<com.example.planes.dto.ActionResponseDto> getPlaneActions(UUID id, PageRequest pageRequest, SortDirection sortDirection);

    void transactionUpdate();
}