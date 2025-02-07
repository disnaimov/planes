package com.example.planes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaneCreateDto {
    @NotNull
    @Min(300)
    private Integer capacity;
    @NotEmpty
    @JsonProperty("type")
    private String type;
    @NotEmpty
    @JsonProperty("status")
    private String status;
    @NotNull
    @JsonProperty("technical_date")
    private LocalDateTime technicalDate;
}
