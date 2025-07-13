package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.MedicationDTO;
import com.hivmedical.medical.entitty.Medication;
import com.hivmedical.medical.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    // Lấy tất cả thuốc đang hoạt động
    public List<MedicationDTO> getAllActiveMedications() {
        return medicationRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy tất cả thuốc
    public List<MedicationDTO> getAllMedications() {
        return medicationRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy thuốc theo ID
    public MedicationDTO getMedicationById(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thuốc không tồn tại với ID: " + id));
        return mapToDTO(medication);
    }

    // Lấy thuốc theo nhóm thuốc
    public List<MedicationDTO> getMedicationsByDrugClass(String drugClass) {
        return medicationRepository.findActiveByDrugClass(drugClass)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Tìm kiếm thuốc theo từ khóa
    public List<MedicationDTO> searchMedications(String keyword) {
        return medicationRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy tất cả nhóm thuốc
    public List<String> getAllDrugClasses() {
        return medicationRepository.findAllActiveDrugClasses();
    }

    // Tạo thuốc mới
    @Transactional
    public MedicationDTO createMedication(MedicationDTO dto) {
        validateMedicationDTO(dto);

        Medication medication = new Medication();
        medication.setName(dto.getName());
        medication.setGenericName(dto.getGenericName());
        medication.setBrandName(dto.getBrandName());
        medication.setDescription(dto.getDescription());
        medication.setDrugClass(dto.getDrugClass());
        medication.setMechanism(dto.getMechanism());
        medication.setDosageForms(dto.getDosageForms());
        medication.setStandardDosage(dto.getStandardDosage());
        medication.setSideEffects(dto.getSideEffects());
        medication.setContraindications(dto.getContraindications());
        medication.setDrugInteractions(dto.getDrugInteractions());
        medication.setActive(dto.isActive());

        Medication savedMedication = medicationRepository.save(medication);
        return mapToDTO(savedMedication);
    }

    // Cập nhật thuốc
    @Transactional
    public MedicationDTO updateMedication(Long id, MedicationDTO dto) {
        validateMedicationDTO(dto);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thuốc không tồn tại với ID: " + id));

        medication.setName(dto.getName());
        medication.setGenericName(dto.getGenericName());
        medication.setBrandName(dto.getBrandName());
        medication.setDescription(dto.getDescription());
        medication.setDrugClass(dto.getDrugClass());
        medication.setMechanism(dto.getMechanism());
        medication.setDosageForms(dto.getDosageForms());
        medication.setStandardDosage(dto.getStandardDosage());
        medication.setSideEffects(dto.getSideEffects());
        medication.setContraindications(dto.getContraindications());
        medication.setDrugInteractions(dto.getDrugInteractions());
        medication.setActive(dto.isActive());

        Medication updatedMedication = medicationRepository.save(medication);
        return mapToDTO(updatedMedication);
    }

    // Xóa thuốc (soft delete)
    @Transactional
    public void deleteMedication(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thuốc không tồn tại với ID: " + id));

        medication.setActive(false);
        medicationRepository.save(medication);
    }

    // Kích hoạt/phục hồi thuốc
    @Transactional
    public MedicationDTO activateMedication(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thuốc không tồn tại với ID: " + id));

        medication.setActive(true);
        Medication activatedMedication = medicationRepository.save(medication);
        return mapToDTO(activatedMedication);
    }

    // Lấy thuốc theo tên
    public MedicationDTO getMedicationByName(String name) {
        Medication medication = medicationRepository.findByNameAndIsActiveTrue(name)
                .orElseThrow(() -> new IllegalArgumentException("Thuốc không tồn tại với tên: " + name));
        return mapToDTO(medication);
    }

    // Lấy thuốc theo tên gốc
    public MedicationDTO getMedicationByGenericName(String genericName) {
        Medication medication = medicationRepository.findByGenericNameAndIsActiveTrue(genericName)
                .orElseThrow(() -> new IllegalArgumentException("Thuốc không tồn tại với tên gốc: " + genericName));
        return mapToDTO(medication);
    }

    // Chuyển đổi Entity sang DTO
    private MedicationDTO mapToDTO(Medication medication) {
        MedicationDTO dto = new MedicationDTO();
        dto.setId(medication.getId());
        dto.setName(medication.getName());
        dto.setGenericName(medication.getGenericName());
        dto.setBrandName(medication.getBrandName());
        dto.setDescription(medication.getDescription());
        dto.setDrugClass(medication.getDrugClass());
        dto.setMechanism(medication.getMechanism());
        dto.setDosageForms(medication.getDosageForms());
        dto.setStandardDosage(medication.getStandardDosage());
        dto.setSideEffects(medication.getSideEffects());
        dto.setContraindications(medication.getContraindications());
        dto.setDrugInteractions(medication.getDrugInteractions());
        dto.setActive(medication.isActive());
        dto.setCreatedAt(medication.getCreatedAt());
        dto.setUpdatedAt(medication.getUpdatedAt());

        return dto;
    }

    // Validate DTO
    private void validateMedicationDTO(MedicationDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống");
        }
        if (dto.getGenericName() == null || dto.getGenericName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên gốc thuốc không được để trống");
        }
        if (dto.getDrugClass() == null || dto.getDrugClass().trim().isEmpty()) {
            throw new IllegalArgumentException("Nhóm thuốc không được để trống");
        }
        if (dto.getStandardDosage() == null || dto.getStandardDosage().trim().isEmpty()) {
            throw new IllegalArgumentException("Liều lượng chuẩn không được để trống");
        }
    }
}