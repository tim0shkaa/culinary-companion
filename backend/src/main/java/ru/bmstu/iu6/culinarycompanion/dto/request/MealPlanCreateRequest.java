package ru.bmstu.iu6.culinarycompanion.dto.request;

import java.util.List;

public class MealPlanCreateRequest {
    
    private String name;
    private String startDate;
    private String endDate;
    private List<MealPlanRecipeRequest> recipes;
    
    public MealPlanCreateRequest() {
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
    
    public List<MealPlanRecipeRequest> getRecipes() {
        return recipes;
    }
    
    public void setRecipes(List<MealPlanRecipeRequest> recipes) {
        this.recipes = recipes;
    }
    
    public static class MealPlanRecipeRequest {
        private Long recipeId;
        private String date;
        private String mealType;
        
        public MealPlanRecipeRequest() {
        }
        
        public Long getRecipeId() {
            return recipeId;
        }
        
        public void setRecipeId(Long recipeId) {
            this.recipeId = recipeId;
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
