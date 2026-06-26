package com.blogapp.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Comment {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)	
private Long id;

private String content;

@ManyToOne
@JoinColumn(name="user_id")
private User user;

@ManyToOne
@JoinColumn(name="post_id")
private Post post;
public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

public Post getPost() {
	return post;
}

public void setPost(Post post) {
	this.post = post;
}


public Comment() {}

public Comment(Long id, String content, User user, Post post) {
	super();
	this.id = id;
	this.setContent(content);
	this.user = user;
	this.post = post;
}

public String getContent() {
	return content;
}

public void setContent(String content) {
	this.content = content;
}

}
