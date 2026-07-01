package com.blogapp.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blogapp.demo.entity.Post;
import com.blogapp.demo.repository.CategoryRepository;
import com.blogapp.demo.repository.LikeRepository;
import com.blogapp.demo.repository.PostRepository;
import com.blogapp.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldReturnPostWhenFoundById() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(1L);

        assertEquals(post, result);
        verify(postRepository).findById(1L);
    }
}
