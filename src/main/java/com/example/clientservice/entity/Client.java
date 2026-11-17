package com.example.clientservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "clients",
       uniqueConstraints = @UniqueConstraint(name = "uk_client_passport", columnNames = {"passport_series", "passport_number"}))
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name", nullable = false, length = 80) private String lastName;
    @Column(name = "first_name", nullable = false, length = 80) private String firstName;
    @Column(name = "middle_name", nullable = false, length = 80) private String middleName;
    @Column(name = "birth_date", nullable = false) private LocalDate birthDate;
    @Column(name = "passport_series", nullable = false, length = 4) private String passportSeries;
    @Column(name = "passport_number", nullable = false, length = 20) private String passportNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false) private City city;

    @Column(name = "residence_address", nullable = false, length = 255) private String residenceAddress;
    @Column(name = "mobile_phone", length = 30) private String mobilePhone;
    @Column(name = "email", length = 150) private String email;
    @Column(name = "employed") private Boolean employed;
    @Column(name = "position", length = 100) private String position;
    @Column(name = "registration_address", nullable = false, length = 255) private String registrationAddress;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "citizenship_id", nullable = false) private Citizenship citizenship;

    public Client() {}

    public Long getId() { return id; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { lastName = v; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { firstName = v; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String v) { middleName = v; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate v) { birthDate = v; }
    public String getPassportSeries() { return passportSeries; }
    public void setPassportSeries(String v) { passportSeries = v; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String v) { passportNumber = v; }
    public City getCity() { return city; }
    public void setCity(City v) { city = v; }
    public String getResidenceAddress() { return residenceAddress; }
    public void setResidenceAddress(String v) { residenceAddress = v; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String v) { mobilePhone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public Boolean getEmployed() { return employed; }
    public void setEmployed(Boolean v) { employed = v; }
    public String getPosition() { return position; }
    public void setPosition(String v) { position = v; }
    public String getRegistrationAddress() { return registrationAddress; }
    public void setRegistrationAddress(String v) { registrationAddress = v; }
    public Citizenship getCitizenship() { return citizenship; }
    public void setCitizenship(Citizenship v) { citizenship = v; }
}