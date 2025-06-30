package com.hivmedical.medical.service;

import com.hivmedical.medical.entitty.EducationalMaterial;
import com.hivmedical.medical.entitty.FacilityInfo;
import com.hivmedical.medical.entitty.NewsArticle;
import com.hivmedical.medical.repository.EducationalMaterialRepository;
import com.hivmedical.medical.repository.FacilityInfoRepository;
import com.hivmedical.medical.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {

    @Autowired
    private FacilityInfoRepository facilityInfoRepository;

    @Autowired
    private EducationalMaterialRepository educationalMaterialRepository;

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    public FacilityInfo getFacilityInfo() {
        return facilityInfoRepository.findById(1L).orElseGet(() -> {
            FacilityInfo info = new FacilityInfo();
            info.setTitle("Hệ thống chuyên sâu điều trị HIV");
            info.setDescription("Chúng tôi là cơ sở y tế chuyên sâu trong điều trị HIV, với đội ngũ bác sĩ giàu kinh nghiệm, tận tâm và hệ thống trang thiết bị hiện đại, bảo mật.");
            info.setImageUrl("/assets/hiv-care.png");
            return facilityInfoRepository.save(info);
        });
    }

    public List<EducationalMaterial> getEducationalMaterials() {
        return educationalMaterialRepository.findAll();
    }

    public List<NewsArticle> getNewsArticles() {
        return newsArticleRepository.findAll();
    }
}
