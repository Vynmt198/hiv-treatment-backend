package com.hivmedical.medical.dto;

import java.util.List;

public class ServiceDTO {
  private Long id;
  private String name;
  private List<String> description; // Mảng các chuỗi mô tả
  private String price;
  private String type;


  public ServiceDTO() {
  }

  public ServiceDTO(Long id, String name, List<String> description, String price, String type) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.type = type;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getDescription() {
    return description;
  }

  public void setDescription(List<String> description) {
    this.description = description;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
