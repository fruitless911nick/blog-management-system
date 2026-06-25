package com.blogapp.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.blogapp.demo.entity.Comment;
import com.blogapp.demo.entity.Post;
import com.blogapp.demo.entity.User;
import com.blogapp.demo.repository.CommentRepository;
import com.blogapp.demo.repository.PostRepository;
import com.blogapp.demo.repository.UserRepository;

@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/comment/add")
    public String addComment(
            @RequestParam Long postId,
            @RequestParam String content,
            Principal principal) {

        User user =
            userRepository.findByEmail(
                    principal.getName());

        Post post =
            postRepository.findById(postId)
                    .orElse(null);

        Comment comment = new Comment();

        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);

        commentRepository.save(comment);

        return "redirect:/post/" + postId;
    }
    @GetMapping("/search")
    public String searchPosts(
            @RequestParam String keyword,
            Model model) {

        model.addAttribute(
                "posts",
                postRepository
                    .findByTitleContainingIgnoreCase(
                            keyword));

        return "home";
    }
}