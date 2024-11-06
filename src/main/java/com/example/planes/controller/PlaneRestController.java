package com.example.planes.controller;

import com.example.planes.dto.PlaneCreateDto;
import com.example.planes.dto.PlaneFilterDto;
import com.example.planes.dto.PlaneResponseDto;
import com.example.planes.service.PlaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/planes")
@RequiredArgsConstructor
public class PlaneRestController {
    private final PlaneService planeService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UUID> create(@RequestBody PlaneCreateDto createDto) {
        return new ResponseEntity<>(planeService.create(createDto), CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<PlaneResponseDto>> getAll(@RequestParam(required = false, defaultValue = "0") int page,
                                                         @RequestParam(required = false, defaultValue = "20") int size) {
        return new ResponseEntity<>(planeService.getAll(PageRequest.of(page, size)), OK);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public ResponseEntity<List<PlaneResponseDto>> getWithFilters(@RequestParam(required = false) Integer capacity, @RequestParam(required = false) String type, @RequestParam(required = false) String status) {
        return new ResponseEntity<>(planeService.filterPlanes(capacity, type, status), OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> registerPlane(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(planeService.registerPlane(id), OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") UUID id) {
        planeService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @RequestMapping(value = "/service", method = RequestMethod.PATCH)
    public ResponseEntity<HttpStatus> technicalService() {
        planeService.technicalService();
        return new ResponseEntity<>(OK);
    }
}
