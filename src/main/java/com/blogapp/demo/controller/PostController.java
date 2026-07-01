package com.blogapp.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.blogapp.demo.entity.Post;
import com.blogapp.demo.entity.User;
import com.blogapp.demo.service.PostService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PostController {
	@Autowired
	private PostService postService;
	@GetMapping("/post/create")
	public String createPostPage(Model model,Principal principal) {
	    model.addAttribute("categories", postService.getAllCategories());
	    User user = postService.getUserByEmail(principal.getName());
	    model.addAttribute("user", user);
	    return "create-post";
	}
	@PostMapping("/post/save")
	public String savePost(@ModelAttribute Post post, @RequestParam("imageFile") MultipartFile imageFile,
	        Principal principal,Model model) throws Exception {

	    User user = postService.getUserByEmail(principal.getName());
	    postService.savePost(post, imageFile, user);

	    return "redirect:/";
	}
	@PostMapping("/post/{id}/like")
	public String likePost(@PathVariable Long id, Principal principal) {
	    User user = postService.getUserByEmail(principal.getName());
	    postService.toggleLike(id, user);
	    return "redirect:/";
	}
	@GetMapping("/")
public String home(@RequestParam(defaultValue = "0") int page, Model model,Principal principal,HttpServletResponse response) {
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	    response.setHeader("Pragma", "no-cache");
	    response.setDateHeader("Expires", 0);
	    
	    User user = postService.getUserByEmail(principal.getName());
	    model.addAttribute("user", user);
	    Pageable pageable = PageRequest.of(page, 5 , Sort.by("createdAt").descending());

	    Page<Post> postPage = postService.getPosts(pageable);
	    model.addAttribute("likeCounts", postService.getLikeCounts(postPage.getContent()));

	    User currentUser = postService.getUserByEmail(principal.getName());
	    model.addAttribute("likedPosts", postService.getLikedPosts(postPage.getContent(), currentUser));

	    model.addAttribute("posts", postPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", postPage.getTotalPages());

	    return "home";
	}
	@GetMapping("/post/{id}")
	public String viewPost( @PathVariable Long id,Model model,Principal principal) {

	    Post post = postService.getPostById(id);
	    User user = postService.getUserByEmail(principal.getName());
	    model.addAttribute("user", user);
	    if (post == null) {
	        return "redirect:/";
	    }

	    model.addAttribute("post", post);
	    model.addAttribute("likeCount", postService.getLikeCount(post));

	    User currentUser = postService.getUserByEmail(principal.getName());
	    model.addAttribute("liked", postService.isPostLikedByUser(post, currentUser));

	    return "view-post";
	}
	@GetMapping("/post/edit/{id}")
	public String editPostPage(@PathVariable Long id, Model model,Principal principal) {
		User user = postService.getUserByEmail(principal.getName());
		model.addAttribute("user", user);
	    Post post = postService.getPostById(id);

	    if (post == null || !postService.isPostAuthor(post, principal.getName())) {
	        return "redirect:/";
	    }

	    model.addAttribute("post", post);

	    return "edit-post";
	}
@PostMapping("/post/update")
public String updatePost(@ModelAttribute Post updatedPost,Principal principal,Model model) {
	User user = postService.getUserByEmail(principal.getName());
	model.addAttribute("user", user);
    Post updated = postService.updatePost(updatedPost, principal.getName());

    if (updated == null) {
        return "redirect:/";
    }

    return "redirect:/post/"+ updated.getId();
}
@GetMapping("/post/delete/{id}")
public String deletePost( @PathVariable Long id,Principal principal) {

	if (!postService.deletePost(id, principal.getName())) {
	    return "redirect:/";
	}

	return "redirect:/";

}
@GetMapping("/profile")
public String profile(Principal principal,Model model) {

    User user = postService.getUserByEmail(principal.getName());

    model.addAttribute("user", user);
    model.addAttribute("posts", postService.getPostsByAuthorId(user.getId()));

    return "profile";
}
@GetMapping("/search")
public String searchPosts(@RequestParam String keyword,@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {

    Pageable pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());

    Page<Post> postPage = postService.searchPosts(keyword, pageable);

    User currentUser = postService.getUserByEmail(principal.getName());

    model.addAttribute("user", currentUser);
    model.addAttribute("likeCounts", postService.getLikeCounts(postPage.getContent()));
    model.addAttribute("likedPosts", postService.getLikedPosts(postPage.getContent(), currentUser));
    model.addAttribute("posts", postPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", postPage.getTotalPages());
    model.addAttribute("keyword", keyword);
    model.addAttribute("isSearch", true);
    return "home";
}
}
