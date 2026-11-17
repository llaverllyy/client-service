package com.example.clientservice.controller;

import com.example.clientservice.dto.ClientDto;
import com.example.clientservice.entity.City;
import com.example.clientservice.entity.Citizenship;
import com.example.clientservice.repository.CityRepository;
import com.example.clientservice.repository.CitizenshipRepository;
import com.example.clientservice.service.ClientService;
import com.example.clientservice.service.JsonTransferService;
import com.example.clientservice.service.XmlTransferService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientController {
    private final ClientService clients;
    private final XmlTransferService xml;
    private final JsonTransferService json;
    private final CityRepository cities;
    private final CitizenshipRepository citizenships;

    public ClientController(ClientService clients, XmlTransferService xml, JsonTransferService json,
                            CityRepository cities, CitizenshipRepository citizenships) {
        this.clients = clients; this.xml = xml; this.json = json;
        this.cities = cities; this.citizenships = citizenships;
    }

    @GetMapping("/clients")
    public List<ClientDto> findAll() { return clients.findAll(); }

    @PostMapping(value = "/clients/xml", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ClientDto createFromXml(@RequestBody String body) {
        return xml.createClientXml(body);
    }

    @PutMapping(value = "/clients/{id}/xml", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ClientDto updateFromXml(@PathVariable Long id, @RequestBody String body) {
        return xml.updateClientXml(id, body);
    }

    @GetMapping(value = "/clients/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> findByIdXml(@PathVariable Long id) {
        byte[] body = xml.exportClientXml(id).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok().contentType(new MediaType("application", "xml", StandardCharsets.UTF_8))
                .body(body);
    }

    @GetMapping("/clients/{id}")
    public ClientDto findById(@PathVariable Long id) { return clients.findById(id); }

    @PostMapping("/clients")
    public ResponseEntity<ClientDto> create(@RequestBody ClientDto dto) {
        return ResponseEntity.status(201).body(clients.create(dto));
    }

    @PutMapping("/clients/{id}")
    public ClientDto update(@PathVariable Long id, @RequestBody ClientDto dto) {
        return clients.update(id, dto);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clients.delete(id); return ResponseEntity.noContent().build();
    }

    @GetMapping("/cities")
    public List<City> cities() { return cities.findAll(); }

    @GetMapping("/citizenships")
    public List<Citizenship> citizenships() { return citizenships.findAll(); }

    @PostMapping(value = "/import/xml", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ResponseEntity<String> importXml(@RequestBody String body) {
        int count = xml.importXml(body);
        return ResponseEntity.ok("Импортировано клиентов: " + count);
    }

    @GetMapping(value = "/export/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> exportXml() {
        byte[] body = xml.exportXml().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clients.xml")
                .contentType(new MediaType("application", "xml", StandardCharsets.UTF_8)).body(body);
    }

    @PostMapping(value = "/import/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importJson(@RequestBody String body) {
        int count = json.importJson(body);
        return ResponseEntity.ok("Импортировано клиентов: " + count);
    }

    @GetMapping(value = "/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportJson() {
        byte[] body = json.exportJson().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clients.json")
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8)).body(body);
    }
}