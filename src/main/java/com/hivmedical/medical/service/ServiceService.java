package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.ServiceDTO;
import com.hivmedical.medical.entitty.ServiceEntity;
import com.hivmedical.medical.repository.ServiceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {
  private static final Logger logger = LoggerFactory.getLogger(ServiceService.class);

  @Autowired
  private ServiceRepository serviceRepository;

  @Autowired
  private ObjectMapper objectMapper;

  public List<ServiceDTO> getAllServices() {
    return serviceRepository.findAll().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  public List<ServiceDTO> getServicesByType(String type) {
    if (!type.equals("FIRST_VISIT") && !type.equals("FOLLOW_UP")) {
      throw new IllegalArgumentException("Type must be FIRST_VISIT or FOLLOW_UP");
    }
    return serviceRepository.findByType(type).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  public ServiceDTO getServiceById(Long id) {
    ServiceEntity entity = serviceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Service with ID " + id + " not found"));
    return mapToDTO(entity);
  }

  public ServiceDTO createService(ServiceDTO dto) {
    if (dto.getType() == null || (!dto.getType().equals("FIRST_VISIT") && !dto.getType().equals("FOLLOW_UP"))) {
      throw new IllegalArgumentException("Type must be FIRST_VISIT or FOLLOW_UP");
    }
    ServiceEntity entity = new ServiceEntity();
    entity.setName(dto.getName());
    try {
      entity.setDescription(objectMapper.writeValueAsString(dto.getDescription()));
    } catch (Exception e) {
      logger.error("Failed to serialize description for service: {}", dto.getName(), e);
      throw new RuntimeException("Failed to serialize description: " + e.getMessage());
    }
    entity.setPrice(dto.getPrice());
    entity.setType(dto.getType());
    ServiceEntity saved = serviceRepository.save(entity);
    return mapToDTO(saved);
  }

  public ServiceDTO updateService(Long id, ServiceDTO dto) {
    ServiceEntity entity = serviceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Service with ID " + id + " not found"));
    entity.setName(dto.getName());
    try {
      entity.setDescription(objectMapper.writeValueAsString(dto.getDescription()));
    } catch (Exception e) {
      logger.error("Failed to serialize description for service ID {}: {}", id, e.getMessage());
      throw new RuntimeException("Failed to serialize description: " + e.getMessage());
    }
    entity.setPrice(dto.getPrice());
    entity.setType(dto.getType());
    ServiceEntity updated = serviceRepository.save(entity);
    return mapToDTO(updated);
  }

  public void deleteService(Long id) {
    if (!serviceRepository.existsById(id)) {
      throw new RuntimeException("Service with ID " + id + " not found");
    }
    serviceRepository.deleteById(id);
  }

  private ServiceDTO mapToDTO(ServiceEntity entity) {
    ServiceDTO dto = new ServiceDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    try {
      if (entity.getDescription() == null || entity.getDescription().isEmpty()) {
        logger.warn("Description is null or empty for service ID {}", entity.getId());
        dto.setDescription(new ArrayList<>());
      } else {
        try {
          objectMapper.readTree(entity.getDescription());
          List<String> description = objectMapper.readValue(
              entity.getDescription(),
              new TypeReference<List<String>>() {}
          );
          dto.setDescription(description);
        } catch (Exception e) {
          logger.error("Invalid JSON format for description in service ID {}: {}", entity.getId(), entity.getDescription());
          throw new RuntimeException("Failed to deserialize description for service ID " + entity.getId() + ": Invalid JSON format");
        }
      }
    } catch (Exception e) {
      logger.error("Failed to deserialize description for service ID {}: {}", entity.getId(), e.getMessage());
      throw new RuntimeException("Failed to deserialize description for service ID " + entity.getId() + ": " + e.getMessage());
    }
    dto.setPrice(entity.getPrice());
    dto.setType(entity.getType());
    return dto;
  }
}