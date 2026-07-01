package com.blogapp.demo.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blogapp.demo.entity.Category;
import com.blogapp.demo.entity.Like;
import com.blogapp.demo.entity.Post;
import com.blogapp.demo.entity.User;
import com.blogapp.demo.repository.CategoryRepository;
import com.blogapp.demo.repository.LikeRepository;
import com.blogapp.demo.repository.PostRepository;
import com.blogapp.demo.repository.UserRepository;

@Service
public class PostService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post savePost(Post post, MultipartFile imageFile, User user) throws Exception {
        post.setAuthor(user);

        if (post.getCategory() != null && post.getCategory().getId() != null) {
            Category category = categoryRepository.findById(post.getCategory().getId()).orElse(null);
            post.setCategory(category);
        }

        if (!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName));
            post.setImageName(fileName);
        }

        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            return;
        }

        if (likeRepository.existsByUserAndPost(user, post)) {
            likeRepository.deleteByUserAndPost(user, post);
        } else {
            likeRepository.save(new Like(user, post));
        }
    }

    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Map<Long, Long> getLikeCounts(List<Post> posts) {
        Map<Long, Long> likeCounts = new HashMap<>();
        for (Post post : posts) {
            likeCounts.put(post.getId(), likeRepository.countByPost(post));
        }
        return likeCounts;
    }

    public Map<Long, Boolean> getLikedPosts(List<Post> posts, User currentUser) {
        Map<Long, Boolean> likedPosts = new HashMap<>();
        for (Post post : posts) {
            likedPosts.put(post.getId(), likeRepository.existsByUserAndPost(currentUser, post));
        }
        return likedPosts;
    }

    public long getLikeCount(Post post) {
        return likeRepository.countByPost(post);
    }

    public boolean isPostLikedByUser(Post post, User currentUser) {
        return likeRepository.existsByUserAndPost(currentUser, post);
    }

    public boolean isPostAuthor(Post post, String email) {
        return post != null
                && post.getAuthor() != null
                && post.getAuthor().getEmail() != null
                && post.getAuthor().getEmail().equals(email);
    }

    public Post updatePost(Post updatedPost, String email) {
        Post existingPost = postRepository.findById(updatedPost.getId()).orElse(null);

        if (existingPost == null || !isPostAuthor(existingPost, email)) {
            return null;
        }

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        return postRepository.save(existingPost);
    }

    public boolean deletePost(Long id, String email) {
        Post post = postRepository.findById(id).orElse(null);

        if (post == null || !isPostAuthor(post, email)) {
            return false;
        }

        postRepository.delete(post);
        return true;
    }

    public List<Post> getPostsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchPosts(keyword, pageable);
    }
}
