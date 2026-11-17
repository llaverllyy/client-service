package com.example.clientservice.service;

import com.example.clientservice.dto.ClientDto;
import com.example.clientservice.entity.City;
import com.example.clientservice.entity.Citizenship;
import com.example.clientservice.entity.Client;
import com.example.clientservice.repository.CityRepository;
import com.example.clientservice.repository.CitizenshipRepository;
import com.example.clientservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClientServiceTest {
    private ClientRepository clients;
    private CityRepository cities;
    private CitizenshipRepository citizenships;
    private ClientService service;

    @BeforeEach
    void setUp() {
        clients = mock(ClientRepository.class);
        cities = mock(CityRepository.class);
        citizenships = mock(CitizenshipRepository.class);
        service = new ClientService(clients, cities, citizenships);
        when(cities.findByNameIgnoreCase("Минск")).thenReturn(Optional.of(new City("Минск")));
        when(citizenships.findByNameIgnoreCase("Республика Беларусь"))
                .thenReturn(Optional.of(new Citizenship("Республика Беларусь")));
        when(clients.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private ClientDto validDto() {
        return new ClientDto(null, "Иванов", "Иван", "Иванович",
                LocalDate.of(1998, 4, 15), "MP", "1234567", "Минск",
                "Минск, ул. Ленина, 10", "+375291234567", null, true, "Инженер",
                "Минск, ул. Ленина, 10", "Республика Беларусь");
    }

    @Test
    void createSavesClientAndReturnsDto() {
        when(clients.existsByPassportSeriesAndPassportNumber("MP", "1234567")).thenReturn(false);
        ClientDto result = service.create(validDto());
        assertEquals("Иванов", result.lastName());
        assertEquals("Минск", result.city());
        verify(clients).save(any(Client.class));
    }

    @Test
    void createRejectsDuplicatePassport() {
        when(clients.existsByPassportSeriesAndPassportNumber("MP", "1234567")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> service.create(validDto()));
        verify(clients, never()).save(any());
    }

    @Test
    void createRejectsMissingRequiredField() {
        ClientDto invalid = new ClientDto(null, "", "Иван", "Иванович",
                LocalDate.of(1998, 4, 15), "MP", "1234567", "Минск",
                "Минск, ул. Ленина, 10", null, null, null, null,
                "Минск, ул. Ленина, 10", "Республика Беларусь");
        assertThrows(ResponseStatusException.class, () -> service.create(invalid));
        verify(clients, never()).save(any());
    }
}
