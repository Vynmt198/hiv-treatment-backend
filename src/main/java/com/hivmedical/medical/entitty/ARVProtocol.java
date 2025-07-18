package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "arv_protocols")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ARVProtocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false, length = 255)
    private String name; // Ví dụ: "TDF + 3TC + DTG"

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Nationalized
    @Column(nullable = false, length = 100)
    private String targetGroup; // "Pregnant women", "Children", "Adults", "Elderly"

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String medications; // JSON string chứa danh sách thuốc

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String dosage; // Hướng dẫn liều lượng

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String contraindications; // Chống chỉ định

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String sideEffects; // Tác dụng phụ

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String monitoring; // Theo dõi cần thiết

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
