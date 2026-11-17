package com.example.clientservice.service;

import com.example.clientservice.dto.ClientDto;
import com.example.clientservice.entity.*;
import com.example.clientservice.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ClientService {
    private final ClientRepository clients;
    private final CityRepository cities;
    private final CitizenshipRepository citizenships;

    public ClientService(ClientRepository clients, CityRepository cities, CitizenshipRepository citizenships) {
        this.clients = clients;
        this.cities = cities;
        this.citizenships = citizenships;
    }

    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        return clients.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ClientDto findById(Long id) {
        return toDto(getClient(id));
    }

    public ClientDto create(ClientDto dto) {
        validate(dto);
        String series = normalizeSeries(dto.passportSeries());
        String number = dto.passportNumber().trim();
        if (clients.existsByPassportSeriesAndPassportNumber(series, number))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Клиент с такой серией и номером паспорта уже существует");
        Client client = new Client();
        apply(client, dto);
        return toDto(clients.save(client));
    }

    public ClientDto update(Long id, ClientDto dto) {
        validate(dto);
        Client client = getClient(id);
        String series = normalizeSeries(dto.passportSeries());
        String number = dto.passportNumber().trim();
        boolean passportChanged = !client.getPassportSeries().equals(series)
                || !client.getPassportNumber().equals(number);
        if (passportChanged && clients.existsByPassportSeriesAndPassportNumber(series, number))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Такой паспорт уже зарегистрирован");
        apply(client, dto);
        return toDto(clients.save(client));
    }

    public void delete(Long id) {
        clients.delete(getClient(id));
    }

    public ClientDto createOrUpdateByPassport(ClientDto dto) {
        validate(dto);
        return clients.findByPassportSeriesAndPassportNumber(normalizeSeries(dto.passportSeries()), dto.passportNumber().trim())
                .map(c -> update(c.getId(), dto))
                .orElseGet(() -> create(dto));
    }

    private Client getClient(Long id) {
        return clients.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Клиент с id " + id + " не найден"));
    }

    private void validate(ClientDto d) {
        if (d == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Данные клиента не переданы");
        required(d.lastName(), "Фамилия"); required(d.firstName(), "Имя");
        required(d.middleName(), "Отчество"); required(d.birthDate(), "Дата рождения");
        required(d.passportSeries(), "Серия паспорта"); required(d.passportNumber(), "Номер паспорта");
        required(d.city(), "Город проживания"); required(d.residenceAddress(), "Адрес фактического проживания");
        required(d.registrationAddress(), "Адрес прописки"); required(d.citizenship(), "Гражданство");
        if (d.birthDate() != null && d.birthDate().isAfter(java.time.LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата рождения не может быть в будущем");
    }

    private void required(Object value, String field) {
        if (value == null || value.toString().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поле «" + field + "» обязательно");
    }

    private void apply(Client c, ClientDto d) {
        c.setLastName(d.lastName().trim());
        c.setFirstName(d.firstName().trim());
        c.setMiddleName(d.middleName().trim());
        c.setBirthDate(d.birthDate());
        c.setPassportSeries(d.passportSeries().trim().toUpperCase());
        c.setPassportNumber(d.passportNumber().trim());
        c.setCity(cities.findByNameIgnoreCase(d.city().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неизвестный город: " + d.city())));
        c.setResidenceAddress(d.residenceAddress().trim());
        c.setMobilePhone(blankToNull(d.mobilePhone()));
        c.setEmail(blankToNull(d.email()));
        c.setEmployed(d.employed());
        c.setPosition(blankToNull(d.position()));
        c.setRegistrationAddress(d.registrationAddress().trim());
        c.setCitizenship(citizenships.findByNameIgnoreCase(d.citizenship().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неизвестное гражданство: " + d.citizenship())));
    }

    private String blankToNull(String s) { return s == null || s.isBlank() ? null : s.trim(); }
    private String normalizeSeries(String value) { return value.trim().toUpperCase(java.util.Locale.ROOT); }

    private ClientDto toDto(Client c) {
        return new ClientDto(c.getId(), c.getLastName(), c.getFirstName(), c.getMiddleName(),
                c.getBirthDate(), c.getPassportSeries(), c.getPassportNumber(), c.getCity().getName(),
                c.getResidenceAddress(), c.getMobilePhone(), c.getEmail(), c.getEmployed(),
                c.getPosition(), c.getRegistrationAddress(), c.getCitizenship().getName());
    }
}