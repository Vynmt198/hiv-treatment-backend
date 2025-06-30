package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.ServiceDTO;
import com.hivmedical.medical.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

  @Autowired
  private ServiceService serviceService;

  @GetMapping
  public ResponseEntity<List<ServiceDTO>> getAllServices() {
    return ResponseEntity.ok(serviceService.getAllServices());
  }

  @GetMapping("/type/{type}")
  public ResponseEntity<List<ServiceDTO>> getServicesByType(@PathVariable String type) {
    return ResponseEntity.ok(serviceService.getServicesByType(type));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
    return ResponseEntity.ok(serviceService.getServiceById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
  public ResponseEntity<ServiceDTO> createService(@Valid @RequestBody ServiceDTO serviceDTO) {
    return ResponseEntity.ok(serviceService.createService(serviceDTO));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
  public ResponseEntity<ServiceDTO> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDTO serviceDTO) {
    return ResponseEntity.ok(serviceService.updateService(id, serviceDTO));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
  public ResponseEntity<Void> deleteService(@PathVariable Long id) {
    serviceService.deleteService(id);
    return ResponseEntity.noContent().build();
  }
}