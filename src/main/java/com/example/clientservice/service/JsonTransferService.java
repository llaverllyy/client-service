package com.example.clientservice.service;

import com.example.clientservice.dto.ClientDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

@Service
public class JsonTransferService {
    private final ClientService clientService;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting().create();

    private static class LocalDateAdapter implements com.google.gson.JsonSerializer<LocalDate>,
            com.google.gson.JsonDeserializer<LocalDate> {
        @Override
        public com.google.gson.JsonElement serialize(LocalDate src, Type type,
                com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }
        @Override
        public LocalDate deserialize(com.google.gson.JsonElement json, Type type,
                com.google.gson.JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }

    public JsonTransferService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Transactional
    public int importJson(String json) {
        Type listType = new TypeToken<List<ClientDto>>() {}.getType();
        try {
            List<ClientDto> values = gson.fromJson(json, listType);
            if (values == null) throw new IllegalArgumentException("JSON должен содержать массив клиентов");
            values.forEach(clientService::createOrUpdateByPassport);
            return values.size();
        } catch (com.google.gson.JsonParseException ex) {
            throw new IllegalArgumentException("Некорректный JSON: " + ex.getMessage(), ex);
        }
    }

    @Transactional(readOnly = true)
    public String exportJson() {
        return gson.toJson(clientService.findAll());
    }
}