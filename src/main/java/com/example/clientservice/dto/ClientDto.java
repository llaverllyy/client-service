package com.example.clientservice.dto;

import java.time.LocalDate;

public record ClientDto(
        Long id,
        String lastName,
        String firstName,
        String middleName,
        LocalDate birthDate,
        String passportSeries,
        String passportNumber,
        String city,
        String residenceAddress,
        String mobilePhone,
        String email,
        Boolean employed,
        String position,
        String registrationAddress,
        String citizenship
) {}