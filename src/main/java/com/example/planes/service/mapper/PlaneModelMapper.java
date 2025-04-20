package com.example.planes.service.mapper;

import com.example.planes.controller.dto.PlaneCreateDto;
import com.example.planes.service.model.PlaneCreateModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlaneModelMapper {
    PlaneModelMapper INSTANCE = Mappers.getMapper(PlaneModelMapper.class);

    PlaneCreateModel toModel(PlaneCreateDto createDto);
    //PlaneFilterModel toModel(PlaneFilterDto filterDto);
}
