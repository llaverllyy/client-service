package com.example.clientservice.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class XmlTransferServiceTest {
    @Test
    void importsXmlValidatedAgainstXsd() {
        ClientService clientService = mock(ClientService.class);
        XmlTransferService transfer = new XmlTransferService(clientService);
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <clients>
                  <client>
                    <lastName>Тестов</lastName>
                    <firstName>Тест</firstName>
                    <middleName>Тестович</middleName>
                    <birthDate>2000-01-01</birthDate>
                    <passportSeries>MP</passportSeries>
                    <passportNumber>1111111</passportNumber>
                    <city>Минск</city>
                    <residenceAddress>Минск, улица 1</residenceAddress>
                    <registrationAddress>Минск, улица 1</registrationAddress>
                    <citizenship>Республика Беларусь</citizenship>
                  </client>
                </clients>
                """;

        assertEquals(1, transfer.importXml(xml));
        verify(clientService).createOrUpdateByPassport(argThat(dto ->
                dto.lastName().equals("Тестов") && dto.passportNumber().equals("1111111")
                        && dto.birthDate().toString().equals("2000-01-01")));
    }
}
