package com.blogapp.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Post {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String title;
@Column(length=5000)
private String content;
private LocalDateTime createdAt;

@ManyToOne
@JoinColumn(name="user_id")
private User author;

@ManyToOne
@JoinColumn(name = "category_id")
private Category category;

@OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
private List<Comment> comments;
private String imageName;

public Post(Long id, String title, String content, LocalDateTime createdAt, User author, Category category,
		List<Comment> comments, String imageName) {
	super();
	this.id = id;
	this.title = title;
	this.content = content;
	this.createdAt = createdAt;
	this.author = author;
	this.category = category;
	this.comments = comments;
	this.imageName = imageName;
}
public Post() {};
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public User getAuthor() {
	return author;
}
public void setAuthor(User author) {
	this.author = author;
}

public LocalDateTime getCreatedAt() {
	return createdAt;
}
public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public List<Comment> getComments() {
    return comments;
}
public Category getCategory() {
    return category;
}

public void setCategory(Category category) {
    this.category = category;
}
public String getImageName() {
    return imageName;
}

public void setImageName(String imageName) {
    this.imageName = imageName;
}
}
