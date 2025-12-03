package ru.bmstu.iu6.culinarycompanion.domain;

import java.time.LocalDateTime;

public class Rating {
    
    private Long id;
    private Long recipeId;
    private Long userId;
    private Integer rating;
    private LocalDateTime createdAt;
    
    public Rating() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRecipeId() {
        return recipeId;
    }
    
    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
