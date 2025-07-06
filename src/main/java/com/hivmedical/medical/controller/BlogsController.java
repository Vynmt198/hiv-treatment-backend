package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.BlogsDTO;
import com.hivmedical.medical.service.BlogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogsController {
    @Autowired
    private BlogsService blogsService;

    @GetMapping
    public ResponseEntity<List<BlogsDTO>> getAllBlogPosts() {
        return ResponseEntity.ok(blogsService.getAllBlogPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogsDTO> getBlogPostById(@PathVariable("id") Long blogId) {
        return ResponseEntity.ok(blogsService.getBlogPostById(blogId));
    }

    @PostMapping
    public ResponseEntity<BlogsDTO> createBlogPost(@RequestBody BlogsDTO blogDTO) {
        return ResponseEntity.ok(blogsService.createBlogPost(blogDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogsDTO> updateBlogPost(@PathVariable("id") Long blogId, @RequestBody BlogsDTO blogDTO) {
        return ResponseEntity.ok(blogsService.updateBlogPost(blogId, blogDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable("id") Long blogId) {
        blogsService.deleteBlogPost(blogId);
        return ResponseEntity.noContent().build();
    }
}