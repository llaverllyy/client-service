package com.example.clientservice.service;

import com.example.clientservice.dto.ClientDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonTransferServiceTest {
    @Test
    void importsJsonIntoClientService() {
        ClientService clientService = mock(ClientService.class);
        JsonTransferService transfer = new JsonTransferService(clientService);
        String json = """
                [{
                  "lastName":"Тестов",
                  "firstName":"Тест",
                  "middleName":"Тестович",
                  "birthDate":"2000-01-01",
                  "passportSeries":"MP",
                  "passportNumber":"1111111",
                  "city":"Минск",
                  "residenceAddress":"Минск, улица 1",
                  "registrationAddress":"Минск, улица 1",
                  "citizenship":"Республика Беларусь"
                }]
                """;

        assertEquals(1, transfer.importJson(json));
        verify(clientService).createOrUpdateByPassport(argThat(dto ->
                dto.lastName().equals("Тестов") && dto.birthDate().equals(LocalDate.of(2000, 1, 1))));
    }

    @Test
    void exportsJson() {
        ClientService clientService = mock(ClientService.class);
        when(clientService.findAll()).thenReturn(List.of(new ClientDto(1L, "Тестов", "Тест", "Тестович",
                LocalDate.of(2000, 1, 1), "MP", "1111111", "Минск", "Минск, улица 1",
                null, null, null, null, "Минск, улица 1", "Республика Беларусь")));
        JsonTransferService transfer = new JsonTransferService(clientService);

        String result = transfer.exportJson();
        assertTrue(result.contains("\"lastName\": \"Тестов\""));
        assertTrue(result.contains("\"birthDate\": \"2000-01-01\""));
    }
}
