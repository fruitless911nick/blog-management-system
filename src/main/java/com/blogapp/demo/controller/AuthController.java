package com.blogapp.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.blogapp.demo.entity.User;
import com.blogapp.demo.repository.UserRepository;

@Controller
public class AuthController {
	@Autowired
private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	@GetMapping("/register")
	public String registerPage() {
		return "register";
	}
	@GetMapping("/login")
    public String loginPage() {
        return "login";
    }
	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return "redirect:/login";
	}
}
