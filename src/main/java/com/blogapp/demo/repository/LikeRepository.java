package com.blogapp.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.blogapp.demo.entity.Like;
import com.blogapp.demo.entity.Post;
import com.blogapp.demo.entity.User;
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserAndPost(User user, Post post);

    long countByPost(Post post);
    @Modifying
    @Transactional
    void deleteByUserAndPost(User user, Post post);
    

}