package com.hivmedical.medical.entitty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "medications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(nullable = false, length = 255)
    private String name; // Tên thuốc: Tenofovir, Lamivudine, Dolutegravir, etc.

    @Nationalized
    @Column(nullable = false, length = 100)
    private String genericName; // Tên gốc

    @Nationalized
    @Column(length = 100)
    private String brandName; // Tên thương hiệu

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Nationalized
    @Column(nullable = false, length = 50)
    private String drugClass; // NRTI, NNRTI, PI, INSTI, etc.

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String mechanism; // Cơ chế tác dụng

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String dosageForms; // Dạng bào chế: viên nén, viên nang, etc.

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String standardDosage; // Liều lượng chuẩn

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String sideEffects; // Tác dụng phụ

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String contraindications; // Chống chỉ định

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String drugInteractions; // Tương tác thuốc

    @Nationalized
    @Column(length = 100)
    private String strength; // Độ mạnh: 300mg, 200mg, etc.

    @Nationalized
    @Column(length = 100)
    private String manufacturer; // Nhà sản xuất

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String storageConditions; // Điều kiện bảo quản

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