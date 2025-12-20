package ru.bmstu.iu6.culinarycompanion.domain;

import ru.bmstu.iu6.culinarycompanion.domain.enums.MealType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MealPlanEntry {
    
    private Long id;
    private Long mealPlanId;
    private Long recipeId;
    private LocalDate mealDate;
    private MealType mealType;
    private LocalDateTime createdAt;
    
    private String recipeTitle;
    private String recipeImageUrl;
    
    public MealPlanEntry() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMealPlanId() {
        return mealPlanId;
    }
    
    public void setMealPlanId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
    }
    
    public Long getRecipeId() {
        return recipeId;
    }
    
    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
    
    public LocalDate getMealDate() {
        return mealDate;
    }
    
    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }
    
    public MealType getMealType() {
        return mealType;
    }
    
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getRecipeTitle() {
        return recipeTitle;
    }
    
    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }
    
    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }
    
    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }
}
