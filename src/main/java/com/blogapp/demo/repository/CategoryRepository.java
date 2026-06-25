package com.blogapp.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.blogapp.demo.entity.Category;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

}