package com.hivmedical.medical.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivmedical.medical.dto.ARVProtocolDTO;
import com.hivmedical.medical.entitty.ARVProtocol;
import com.hivmedical.medical.repository.ARVProtocolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ARVProtocolService {

    @Autowired
    private ARVProtocolRepository arvProtocolRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Lấy tất cả phác đồ ARV đang hoạt động
    public List<ARVProtocolDTO> getAllActiveProtocols() {
        return arvProtocolRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy tất cả phác đồ ARV
    public List<ARVProtocolDTO> getAllProtocols() {
        return arvProtocolRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy phác đồ theo ID
    public ARVProtocolDTO getProtocolById(Long id) {
        ARVProtocol protocol = arvProtocolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phác đồ ARV không tồn tại với ID: " + id));
        return mapToDTO(protocol);
    }

    // Lấy phác đồ theo nhóm đối tượng
    public List<ARVProtocolDTO> getProtocolsByTargetGroup(String targetGroup) {
        return arvProtocolRepository.findByTargetGroupAndIsActiveTrue(targetGroup)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Tìm kiếm phác đồ theo từ khóa
    public List<ARVProtocolDTO> searchProtocols(String keyword) {
        return arvProtocolRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy tất cả nhóm đối tượng
    public List<String> getAllTargetGroups() {
        return arvProtocolRepository.findAllActiveTargetGroups();
    }

    // Tạo phác đồ mới
    @Transactional
    public ARVProtocolDTO createProtocol(ARVProtocolDTO dto) {
        validateProtocolDTO(dto);

        ARVProtocol protocol = new ARVProtocol();
        protocol.setName(dto.getName());
        protocol.setDescription(dto.getDescription());
        protocol.setTargetGroup(dto.getTargetGroup());
        protocol.setDosage(dto.getDosage());
        protocol.setContraindications(dto.getContraindications());
        protocol.setSideEffects(dto.getSideEffects());
        protocol.setMonitoring(dto.getMonitoring());
        protocol.setActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getMedications() != null) {
            try {
                String medicationsJson = objectMapper.writeValueAsString(dto.getMedications());
                protocol.setMedications(medicationsJson);
            } catch (Exception e) {
                throw new IllegalArgumentException("Lỗi khi xử lý danh sách thuốc: " + e.getMessage());
            }
        }

        ARVProtocol savedProtocol = arvProtocolRepository.save(protocol);
        return mapToDTO(savedProtocol);
    }

    // Cập nhật phác đồ
    @Transactional
    public ARVProtocolDTO updateProtocol(Long id, ARVProtocolDTO dto) {
        validateProtocolDTO(dto);

        ARVProtocol protocol = arvProtocolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phác đồ ARV không tồn tại với ID: " + id));

        protocol.setName(dto.getName());
        protocol.setDescription(dto.getDescription());
        protocol.setTargetGroup(dto.getTargetGroup());
        protocol.setDosage(dto.getDosage());
        protocol.setContraindications(dto.getContraindications());
        protocol.setSideEffects(dto.getSideEffects());
        protocol.setMonitoring(dto.getMonitoring());
        protocol.setActive(dto.getIsActive());

        // Chuyển đổi danh sách thuốc thành JSON string
        if (dto.getMedications() != null) {
            try {
                String medicationsJson = objectMapper.writeValueAsString(dto.getMedications());
                protocol.setMedications(medicationsJson);
            } catch (Exception e) {
                throw new IllegalArgumentException("Lỗi khi xử lý danh sách thuốc: " + e.getMessage());
            }
        }

        ARVProtocol updatedProtocol = arvProtocolRepository.save(protocol);
        return mapToDTO(updatedProtocol);
    }

    // Xóa phác đồ (soft delete)
    @Transactional
    public void deleteProtocol(Long id) {
        ARVProtocol protocol = arvProtocolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phác đồ ARV không tồn tại với ID: " + id));

        protocol.setActive(false);
        arvProtocolRepository.save(protocol);
    }

    // Kích hoạt/phục hồi phác đồ
    @Transactional
    public ARVProtocolDTO activateProtocol(Long id) {
        ARVProtocol protocol = arvProtocolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phác đồ ARV không tồn tại với ID: " + id));

        protocol.setActive(true);
        ARVProtocol activatedProtocol = arvProtocolRepository.save(protocol);
        return mapToDTO(activatedProtocol);
    }

    // Chuyển đổi Entity sang DTO
    private ARVProtocolDTO mapToDTO(ARVProtocol protocol) {
        ARVProtocolDTO dto = new ARVProtocolDTO();
        dto.setId(protocol.getId());
        dto.setName(protocol.getName());
        dto.setDescription(protocol.getDescription());
        dto.setTargetGroup(protocol.getTargetGroup());
        dto.setDosage(protocol.getDosage());
        dto.setContraindications(protocol.getContraindications());
        dto.setSideEffects(protocol.getSideEffects());
        dto.setMonitoring(protocol.getMonitoring());
        dto.setIsActive(protocol.isActive());
        dto.setCreatedAt(protocol.getCreatedAt());
        dto.setUpdatedAt(protocol.getUpdatedAt());

        // Chuyển đổi JSON string thành danh sách thuốc
        if (protocol.getMedications() != null) {
            try {
                List<String> medications = objectMapper.readValue(protocol.getMedications(),
                        new TypeReference<List<String>>() {
                        });
                dto.setMedications(medications);
            } catch (Exception e) {
                // Nếu không parse được JSON, trả về null
                dto.setMedications(null);
            }
        }

        return dto;
    }

    // Validate DTO
    private void validateProtocolDTO(ARVProtocolDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phác đồ không được để trống");
        }
        if (dto.getTargetGroup() == null || dto.getTargetGroup().trim().isEmpty()) {
            throw new IllegalArgumentException("Nhóm đối tượng không được để trống");
        }
        if (dto.getDosage() == null || dto.getDosage().trim().isEmpty()) {
            throw new IllegalArgumentException("Liều lượng không được để trống");
        }
    }
}
