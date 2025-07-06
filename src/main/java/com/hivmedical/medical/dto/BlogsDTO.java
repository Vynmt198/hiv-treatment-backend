package com.hivmedical.medical.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BlogsDTO {
    private Long id;
    private String title;
    private String author;
    private LocalDate createdAt;
    private String status;
    private String description;
    private String content;
    private String link;
    private LocalDateTime createdAtCommon;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public LocalDateTime getCreatedAtCommon() { return createdAtCommon; }
    public void setCreatedAtCommon(LocalDateTime createdAtCommon) { this.createdAtCommon = createdAtCommon; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}