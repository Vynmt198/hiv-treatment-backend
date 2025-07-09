package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "test_categories")
@Data
public class TestCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;
}