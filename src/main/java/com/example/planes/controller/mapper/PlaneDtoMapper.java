package com.example.planes.controller.mapper;

import com.example.planes.controller.dto.PlaneDeleteResponseDto;
import com.example.planes.controller.dto.PlaneRegisterResponseDto;
import com.example.planes.controller.dto.PlaneResponseDto;
import com.example.planes.controller.dto.PlaneTOResponseDto;
import com.example.planes.service.model.PlaneDeleteResponseModel;
import com.example.planes.service.model.PlaneRegisterResponseModel;
import com.example.planes.service.model.PlaneResponseModel;
import com.example.planes.service.model.PlaneTOResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlaneDtoMapper {
    PlaneDtoMapper INSTANCE = Mappers.getMapper(PlaneDtoMapper.class);

    PlaneResponseDto toDto(PlaneResponseModel responseModel);
    List<PlaneResponseDto> toDto(List<PlaneResponseModel> planeResponseModels);
    PlaneRegisterResponseDto toDto(PlaneRegisterResponseModel planeRegisterResponseModel);
    PlaneDeleteResponseDto toDto(PlaneDeleteResponseModel planeDeleteResponseModel);
    PlaneTOResponseDto toDto(PlaneTOResponseModel planeTOResponseModel);
}
