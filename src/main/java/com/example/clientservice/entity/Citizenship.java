package com.example.clientservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "citizenships", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Citizenship {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    protected Citizenship() {}
    public Citizenship(String name) { this.name = name; }
    public Long getId() { return id; }
    public String getName() { return name; }
}