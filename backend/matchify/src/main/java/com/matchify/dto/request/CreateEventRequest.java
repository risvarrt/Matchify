package com.matchify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequest {
    @NotBlank
    private String eventName;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate eventDate;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;

    private String additionalInstructions;
    @NotBlank
    private String address;
    @NotBlank
    private String pincode;
    @NotBlank
    private String city;
}
