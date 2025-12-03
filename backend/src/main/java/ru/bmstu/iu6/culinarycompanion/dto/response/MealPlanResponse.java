package ru.bmstu.iu6.culinarycompanion.dto.response;

import java.util.List;

public class MealPlanResponse {
    
    private Long id;
    private Long userId;
    private String name;
    private String startDate;
    private String endDate;
    private List<MealPlanRecipeResponse> recipes;
    private String createdAt;
    
    public MealPlanResponse() {
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public List<MealPlanRecipeResponse> getRecipes() {
        return recipes;
    }
    
    public void setRecipes(List<MealPlanRecipeResponse> recipes) {
        this.recipes = recipes;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public static class MealPlanRecipeResponse {
        private Long id;
        private Long recipeId;
        private String recipeTitle;
        private String date;
        private String mealType;
        
        public MealPlanRecipeResponse() {
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
        
        public String getRecipeTitle() {
            return recipeTitle;
        }
        
        public void setRecipeTitle(String recipeTitle) {
            this.recipeTitle = recipeTitle;
        }
        
        public String getDate() {
            return date;
        }
        
        public void setDate(String date) {
            this.date = date;
        }
        
        public String getMealType() {
            return mealType;
        }
        
        public void setMealType(String mealType) {
            this.mealType = mealType;
        }
    }
}
