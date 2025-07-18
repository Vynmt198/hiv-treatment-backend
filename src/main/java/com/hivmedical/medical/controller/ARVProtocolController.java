package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.ARVProtocolDTO;
import com.hivmedical.medical.service.ARVProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/arv-protocols")
public class ARVProtocolController {

    @Autowired
    private ARVProtocolService arvProtocolService;

    // Lấy tất cả phác đồ ARV đang hoạt động (cho tất cả người dùng)
    @GetMapping("/active")
    public ResponseEntity<List<ARVProtocolDTO>> getAllActiveProtocols() {
        List<ARVProtocolDTO> protocols = arvProtocolService.getAllActiveProtocols();
        return ResponseEntity.ok(protocols);
    }

    // Lấy tất cả phác đồ ARV (chỉ Admin/Doctor)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<ARVProtocolDTO>> getAllProtocols() {
        List<ARVProtocolDTO> protocols = arvProtocolService.getAllProtocols();
        return ResponseEntity.ok(protocols);
    }

    // Lấy phác đồ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ARVProtocolDTO> getProtocolById(@PathVariable Long id) {
        ARVProtocolDTO protocol = arvProtocolService.getProtocolById(id);
        return ResponseEntity.ok(protocol);
    }

    // Lấy phác đồ theo nhóm đối tượng
    @GetMapping("/target-group/{targetGroup}")
    public ResponseEntity<List<ARVProtocolDTO>> getProtocolsByTargetGroup(@PathVariable String targetGroup) {
        List<ARVProtocolDTO> protocols = arvProtocolService.getProtocolsByTargetGroup(targetGroup);
        return ResponseEntity.ok(protocols);
    }

    // Tìm kiếm phác đồ theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<ARVProtocolDTO>> searchProtocols(@RequestParam String keyword) {
        List<ARVProtocolDTO> protocols = arvProtocolService.searchProtocols(keyword);
        return ResponseEntity.ok(protocols);
    }

    // Lấy tất cả nhóm đối tượng
    @GetMapping("/target-groups")
    public ResponseEntity<List<String>> getAllTargetGroups() {
        List<String> targetGroups = arvProtocolService.getAllTargetGroups();
        return ResponseEntity.ok(targetGroups);
    }

    // Tạo phác đồ mới (chỉ Admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ARVProtocolDTO> createProtocol(@Valid @RequestBody ARVProtocolDTO dto) {
        ARVProtocolDTO createdProtocol = arvProtocolService.createProtocol(dto);
        return ResponseEntity.ok(createdProtocol);
    }

    // Cập nhật phác đồ (chỉ Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ARVProtocolDTO> updateProtocol(@PathVariable Long id,
            @Valid @RequestBody ARVProtocolDTO dto) {
        ARVProtocolDTO updatedProtocol = arvProtocolService.updateProtocol(id, dto);
        return ResponseEntity.ok(updatedProtocol);
    }

    // Xóa phác đồ (chỉ Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProtocol(@PathVariable Long id) {
        arvProtocolService.deleteProtocol(id);
        return ResponseEntity.ok().build();
    }

    // Kích hoạt/phục hồi phác đồ (chỉ Admin)
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ARVProtocolDTO> activateProtocol(@PathVariable Long id) {
        ARVProtocolDTO activatedProtocol = arvProtocolService.activateProtocol(id);
        return ResponseEntity.ok(activatedProtocol);
    }
}
