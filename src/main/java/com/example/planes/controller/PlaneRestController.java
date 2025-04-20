package com.example.planes.controller;

import com.example.planes.controller.dto.PlaneCreateDto;
import com.example.planes.controller.dto.PlaneDeleteResponseDto;
import com.example.planes.controller.dto.PlaneRegisterResponseDto;
import com.example.planes.controller.dto.PlaneResponseDto;
import com.example.planes.controller.dto.PlaneTOResponseDto;
import com.example.planes.controller.mapper.PlaneDtoMapper;
import com.example.planes.service.PlaneService;
import com.example.planes.service.mapper.PlaneModelMapper;
import com.example.planes.service.model.PlaneCreateModel;
import com.example.planes.service.model.PlaneResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/planes")
@RequiredArgsConstructor
public class PlaneRestController {
    private final PlaneService planeService;
    private final PlaneModelMapper modelMapper;
    private final PlaneDtoMapper dtoMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UUID> create(@RequestBody PlaneCreateDto createDto) {
        PlaneCreateModel model = modelMapper.toModel(createDto);
        UUID response = planeService.create(model);
        return new ResponseEntity<>(response, CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<PlaneResponseDto>> getAll(@RequestParam(required = false, defaultValue = "0") int page,
                                                         @RequestParam(required = false, defaultValue = "20") int size) {
        List<PlaneResponseModel> planeResponseModels = planeService.getAll(PageRequest.of(page, size));
        List<PlaneResponseDto> dto = dtoMapper.toDto(planeResponseModels);
        //return new ResponseEntity<>(dtoMapper.toDto(planeService.getAll(PageRequest.of(page, size))), OK);
        return new ResponseEntity<>(dto, OK);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public ResponseEntity<List<PlaneResponseDto>> getWithFilters(@RequestParam(required = false) Integer capacity, @RequestParam(required = false) String type, @RequestParam(required = false) String status) {

        return new ResponseEntity<>(dtoMapper.toDto(planeService.filterPlanes(capacity, type, status)), OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<PlaneRegisterResponseDto> registerPlane(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(dtoMapper.toDto(planeService.registerPlane(id)), OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<PlaneDeleteResponseDto> deleteById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(dtoMapper.toDto(planeService.delete(id)), OK);
    }

    @RequestMapping(value = "/service", method = RequestMethod.PATCH)
    public ResponseEntity<PlaneTOResponseDto> technicalService() {
        return new ResponseEntity<>(dtoMapper.toDto(planeService.technicalService()) ,OK);
    }
}
