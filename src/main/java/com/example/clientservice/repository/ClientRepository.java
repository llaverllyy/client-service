package com.example.clientservice.repository;
import com.example.clientservice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByPassportSeriesAndPassportNumber(String series, String number);
    java.util.Optional<Client> findByPassportSeriesAndPassportNumber(String series, String number);
}