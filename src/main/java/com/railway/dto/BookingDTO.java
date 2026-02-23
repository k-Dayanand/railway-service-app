package com.railway.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookingDTO {

    @NotNull(message = "Train is required")
    private Long trainId;

    @NotBlank(message = "Passenger name is required")
    private String passengerName;

    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be less than 120")
    private int passengerAge;

    @NotBlank(message = "Gender is required")
    private String passengerGender;

    @NotBlank(message = "Seat type is required")
    private String seatType;

    @NotBlank(message = "Journey date is required")
    private String journeyDate;
}
