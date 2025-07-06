package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.BlogsDTO;
import com.hivmedical.medical.entitty.Blogs; // Sửa package
import com.hivmedical.medical.repository.BlogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogsService {
    @Autowired
    private BlogsRepository blogsRepository;

    public List<BlogsDTO> getAllBlogPosts() {
        return blogsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BlogsDTO getBlogPostById(Long id) {
        Blogs blog = blogsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết với ID " + id + " không tồn tại"));
        return convertToDTO(blog);
    }

    public BlogsDTO createBlogPost(BlogsDTO blogDTO) {
        Blogs blog = convertToEntity(blogDTO);
        return convertToDTO(blogsRepository.save(blog));
    }

    public BlogsDTO updateBlogPost(Long id, BlogsDTO blogDTO) {
        Blogs existing = blogsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết với ID " + id + " không tồn tại"));
        existing.setTitle(blogDTO.getTitle());
        existing.setAuthor(blogDTO.getAuthor());
        existing.setCreatedAt(blogDTO.getCreatedAt());
        existing.setStatus(blogDTO.getStatus());
        existing.setDescription(blogDTO.getDescription());
        existing.setContent(blogDTO.getContent());
        existing.setLink(blogDTO.getLink());
        return convertToDTO(blogsRepository.save(existing));
    }

    public void deleteBlogPost(Long id) {
        if (!blogsRepository.existsById(id)) {
            throw new RuntimeException("Bài viết với ID " + id + " không tồn tại");
        }
        blogsRepository.deleteById(id);
    }

    private BlogsDTO convertToDTO(Blogs blog) {
        BlogsDTO dto = new BlogsDTO();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setAuthor(blog.getAuthor());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setStatus(blog.getStatus());
        dto.setDescription(blog.getDescription());
        dto.setContent(blog.getContent());
        dto.setLink(blog.getLink());
        dto.setCreatedAtCommon(blog.getCreatedAtCommon());
        dto.setUpdatedAt(blog.getUpdatedAt());
        return dto;
    }

    private Blogs convertToEntity(BlogsDTO dto) {
        Blogs blog = new Blogs();
        blog.setId(dto.getId());
        blog.setTitle(dto.getTitle());
        blog.setAuthor(dto.getAuthor());
        blog.setCreatedAt(dto.getCreatedAt());
        blog.setStatus(dto.getStatus());
        blog.setDescription(dto.getDescription());
        blog.setContent(dto.getContent());
        blog.setLink(dto.getLink());
        return blog;
    }
}