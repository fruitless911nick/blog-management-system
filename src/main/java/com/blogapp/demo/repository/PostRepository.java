package com.blogapp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blogapp.demo.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>{
	@Query("""
			SELECT p FROM Post p
			WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR LOWER(p.author.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR STR(p.createdAt) LIKE CONCAT('%', :keyword, '%')
			""")
			List<Post> searchPosts(@Param("keyword") String keyword);
	List<Post> findByTitleContainingIgnoreCase(String keyword);
	List<Post> findByAuthorId(Long id);
}
