package ru.bmstu.iu6.culinarycompanion.domain;

import java.time.LocalDateTime;

public class ShoppingList {
    
    private Long id;
    private Long userId;
    private Long mealPlanId;
    private LocalDateTime createdAt;
    
    public ShoppingList() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getMealPlanId() {
        return mealPlanId;
    }
    
    public void setMealPlanId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
