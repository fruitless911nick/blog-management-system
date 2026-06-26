package com.blogapp.demo.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.blogapp.demo.entity.Category;
import com.blogapp.demo.entity.Post;
import com.blogapp.demo.entity.User;
import com.blogapp.demo.repository.CategoryRepository;
import com.blogapp.demo.repository.PostRepository;
import com.blogapp.demo.repository.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
@Controller
public class PostController {
	@Autowired
	private PostRepository postRepository;
   @Autowired
   private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/post/create")
	public String createPostPage(Model model) {

	    model.addAttribute(
	            "categories",
	            categoryRepository.findAll());

	    return "create-post";
	}
	@PostMapping("/post/save")
	public String savePost(
	        @ModelAttribute Post post,
	        @RequestParam("imageFile") MultipartFile imageFile,
	        Principal principal)
	        throws Exception {

	    // Logged-in user
	    String email = principal.getName();  // user ka email lega jo login kiya hai

	    User user = userRepository.findByEmail(email);

	    post.setAuthor(user);

	    // Category
	    Category category =categoryRepository.findById(post.getCategory().getId()).orElse(null);

	    post.setCategory(category);

	    // Image Upload
	    if (!imageFile.isEmpty()) {
	        String fileName =imageFile.getOriginalFilename();
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
@GetMapping("/")
public String home(Model model) {

    model.addAttribute("posts",postRepository.findAll());

    return "home";
}
@GetMapping("/post/{id}")
public String viewPost(
        @PathVariable Long id,Model model) {
    Post post = postRepository.findById(id) .orElse(null);
    model.addAttribute("post", post);

    return "view-post";
}
@GetMapping("/post/edit/{id}")
public String editPostPage(
        @PathVariable Long id,
        Model model,
        Principal principal) {

    Post post = postRepository.findById(id).orElse(null);
    if (!post.getAuthor().getEmail().equals(principal.getName())) {

        return "redirect:/";
    }

    String email = principal.getName();

    if (!post.getAuthor().getEmail().equals(email)) {

        return "redirect:/";
    }

    model.addAttribute("post", post);

    return "edit-post";
}
@PostMapping("/post/update")
public String updatePost(
        @ModelAttribute Post updatedPost,Principal principal) {

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
public String searchPosts(@RequestParam String keyword,Model model) {

    model.addAttribute("posts",postRepository.searchPosts(keyword));

    return "home";
}
}
