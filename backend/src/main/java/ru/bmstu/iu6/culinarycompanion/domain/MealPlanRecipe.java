package ru.bmstu.iu6.culinarycompanion.domain;

import java.time.LocalDate;

public class MealPlanRecipe {
    
    private Long id;
    private Long mealPlanId;
    private Long recipeId;
    private LocalDate date;
    private String mealType;
    private String recipeTitle;
    
    public MealPlanRecipe() {
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
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    public String getRecipeTitle() {
        return recipeTitle;
    }
    
    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }
}
