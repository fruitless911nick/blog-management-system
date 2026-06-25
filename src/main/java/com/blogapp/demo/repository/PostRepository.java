package com.blogapp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogapp.demo.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>{
	List<Post> findByTitleContainingIgnoreCase(String keyword);
	List<Post> findByAuthorId(Long id);
}
