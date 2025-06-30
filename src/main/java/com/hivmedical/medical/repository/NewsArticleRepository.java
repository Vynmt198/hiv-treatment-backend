package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
}