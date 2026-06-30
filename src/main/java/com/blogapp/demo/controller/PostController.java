package com.blogapp.demo.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.blogapp.demo.entity.Category;
import com.blogapp.demo.entity.Like;
import com.blogapp.demo.entity.Post;
import com.blogapp.demo.entity.User;
import com.blogapp.demo.repository.CategoryRepository;
import com.blogapp.demo.repository.LikeRepository;
import com.blogapp.demo.repository.PostRepository;
import com.blogapp.demo.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
@Controller
public class PostController {
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private PostRepository postRepository;
   @Autowired
   private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/post/create")
	public String createPostPage(Model model,Principal principal) {
	    model.addAttribute( "categories",categoryRepository.findAll());
	    User user = userRepository.findByEmail(principal.getName());
	    model.addAttribute("user", user);
	    return "create-post";
	}
	@PostMapping("/post/save")
	public String savePost(@ModelAttribute Post post, @RequestParam("imageFile") MultipartFile imageFile,
	        Principal principal,Model model) throws Exception {

	    // Logged-in user
	    String email = principal.getName();  // user ka email lega jo login kiya hai

	    User user = userRepository.findByEmail(email);
	    
	    
	    post.setAuthor(user);

	    // Category
	    Category category =categoryRepository.findById(post.getCategory().getId()).orElse(null);

	    post.setCategory(category);

	    // Image Upload
	    if (!imageFile.isEmpty()) {
	        String fileName =System.currentTimeMillis()+imageFile.getOriginalFilename();
	        Path uploadPath =Paths.get("src/main/resources/static/uploads");

	        // Folder create if not exists
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        Files.copy(imageFile.getInputStream(),uploadPath.resolve(fileName));
	        post.setImageName(fileName);
	    }
	    post.setCreatedAt(LocalDateTime.now());
	    // Save post
	    postRepository.save(post);

	    return "redirect:/";
	}
	@PostMapping("/post/{id}/like")
	public String likePost(@PathVariable Long id, Principal principal) {

	    // Logged in user
	    User user = userRepository.findByEmail(principal.getName());

	    // Current post
	    Post post = postRepository.findById(id).orElse(null);

	    if (post == null) {
	        return "redirect:/";
	    }

	    // Already liked?
	    if (likeRepository.existsByUserAndPost(user, post)) {

	        likeRepository.deleteByUserAndPost(user, post);

	    } else {

	        Like like = new Like(user, post);
	        likeRepository.save(like);
	    }

	    return "redirect:/";
	}
	@GetMapping("/")
public String home(@RequestParam(defaultValue = "0") int page, Model model,Principal principal,HttpServletResponse response) {
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	    response.setHeader("Pragma", "no-cache");
	    response.setDateHeader("Expires", 0);
	    
	    User user = userRepository.findByEmail(principal.getName());
	    model.addAttribute("user", user);
	    Pageable pageable = PageRequest.of(page, 5);

	    Page<Post> postPage = postRepository.findAll(pageable);
	    Map<Long, Long> likeCounts = new HashMap<>();

	    for (Post post : postPage.getContent()) {
	        likeCounts.put(post.getId(),likeRepository.countByPost(post));
	    }

	    model.addAttribute("likeCounts", likeCounts);
	    Map<Long, Boolean> likedPosts = new HashMap<>();

	    User currentUser =userRepository.findByEmail(principal.getName());

	    for(Post post : postPage.getContent()){

	        likedPosts.put(post.getId(), likeRepository.existsByUserAndPost(currentUser,post));
	    }

	    model.addAttribute("likedPosts", likedPosts);

	    model.addAttribute("posts", postPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", postPage.getTotalPages());

	    return "home";
	}
	@GetMapping("/post/{id}")
	public String viewPost( @PathVariable Long id,Model model,Principal principal) {

	    Post post = postRepository.findById(id).orElse(null);
	    User user = userRepository.findByEmail(principal.getName());
	    model.addAttribute("user", user);
	    if (post == null) {
	        return "redirect:/";
	    }

	    model.addAttribute("post", post);

	    // Total Likes
	    long likeCount = likeRepository.countByPost(post);
	    model.addAttribute("likeCount", likeCount);

	    // Current User
	    User currentUser =userRepository.findByEmail(principal.getName());

	    // User already liked?
	    boolean liked =likeRepository.existsByUserAndPost(currentUser, post);

	    model.addAttribute("liked", liked);

	    return "view-post";
	}
	@GetMapping("/post/edit/{id}")
	public String editPostPage(@PathVariable Long id, Model model,Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);
	    Post post = postRepository.findById(id).orElse(null);

	    if (post == null) {
	        return "redirect:/";
	    }

	    // Only the author can edit
	    if (!post.getAuthor().getEmail().equals(principal.getName())) {
	        return "redirect:/";
	    }

	    model.addAttribute("post", post);

	    return "edit-post";
	}
@PostMapping("/post/update")
public String updatePost(@ModelAttribute Post updatedPost,Principal principal,Model model) {
	User user = userRepository.findByEmail(principal.getName());
	model.addAttribute("user", user);
    Post existingPost = postRepository.findById(updatedPost.getId()).orElse(null);

    String email = principal.getName();

    if (!existingPost.getAuthor().getEmail().equals(email)) {
        return "redirect:/";
    }

    existingPost.setTitle(updatedPost.getTitle());

    existingPost.setContent(updatedPost.getContent());

    postRepository.save(existingPost);

    return "redirect:/post/"+ existingPost.getId();
}
@GetMapping("/post/delete/{id}")
public String deletePost( @PathVariable Long id,Principal principal) {

	Post post =postRepository.findById(id).orElse(null);

	if (post == null) {
	    return "redirect:/";
	}

	if (!post.getAuthor().getEmail().equals(principal.getName())) {

	    return "redirect:/";
	}

	postRepository.delete(post);

	return "redirect:/";

}
@GetMapping("/profile")
public String profile(Principal principal,Model model) {

    User user =userRepository.findByEmail(principal.getName());

    model.addAttribute("user", user);

    model.addAttribute("posts",postRepository.findByAuthorId(user.getId()));

    return "profile";
}
@GetMapping("/search")
public String searchPosts(@RequestParam String keyword,@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {

    Pageable pageable = PageRequest.of(page, 5);

    Page<Post> postPage = postRepository.searchPosts(keyword, pageable);

    // Current User
    User currentUser = userRepository.findByEmail(principal.getName());

    model.addAttribute("user", currentUser);

    // Like Counts
    Map<Long, Long> likeCounts = new HashMap<>();

    // Liked Posts
    Map<Long, Boolean> likedPosts = new HashMap<>();

    for (Post post : postPage.getContent()) {

        likeCounts.put(post.getId(),likeRepository.countByPost(post));

        likedPosts.put(post.getId(),likeRepository.existsByUserAndPost(currentUser, post));
    }

    model.addAttribute("likeCounts", likeCounts);
    model.addAttribute("likedPosts", likedPosts);
    model.addAttribute("posts", postPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", postPage.getTotalPages());
    model.addAttribute("keyword", keyword);
    model.addAttribute("isSearch", true);
    return "home";
}
}
