package com.example.clientservice.config;

import com.example.clientservice.entity.City;
import com.example.clientservice.entity.Citizenship;
import com.example.clientservice.repository.CityRepository;
import com.example.clientservice.repository.CitizenshipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DictionarySeeder implements CommandLineRunner {
    private final CityRepository cities;
    private final CitizenshipRepository citizenships;

    public DictionarySeeder(CityRepository cities, CitizenshipRepository citizenships) {
        this.cities = cities;
        this.citizenships = citizenships;
    }

    @Override
    public void run(String... args) {
        if (cities.count() == 0) {
            cities.saveAll(List.of("Минск", "Гомель", "Гродно", "Брест", "Витебск", "Могилёв", "Барановичи")
                    .stream().map(City::new).toList());
        }
        if (citizenships.count() == 0) {
            citizenships.saveAll(List.of("Республика Беларусь", "Российская Федерация", "Украина",
                    "Польша", "Литва", "Латвия", "Другое").stream().map(Citizenship::new).toList());
        }
    }
}