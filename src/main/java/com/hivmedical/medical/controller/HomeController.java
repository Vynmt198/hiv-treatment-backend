package com.hivmedical.medical.controller;

import com.hivmedical.medical.entitty.EducationalMaterial;
import com.hivmedical.medical.entitty.FacilityInfo;
import com.hivmedical.medical.entitty.NewsArticle;
import com.hivmedical.medical.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/facility-info")
    public ResponseEntity<FacilityInfo> getFacilityInfo() {
        return ResponseEntity.ok(homeService.getFacilityInfo());
    }

    @GetMapping("/educational-materials")
    public ResponseEntity<List<EducationalMaterial>> getEducationalMaterials() {
        return ResponseEntity.ok(homeService.getEducationalMaterials());
    }

    @GetMapping("/news-articles")
    public ResponseEntity<List<NewsArticle>> getNewsArticles() {
        return ResponseEntity.ok(homeService.getNewsArticles());
    }
}