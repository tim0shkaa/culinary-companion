package ru.bmstu.iu6.culinarycompanion.dto.request;

public class MealPlanEntryRequest {
    
    private Long recipeId;
    private String mealDate;
    private String mealType;
    
    public MealPlanEntryRequest() {
    }
    
    public Long getRecipeId() {
        return recipeId;
    }
    
    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
    
    public String getMealDate() {
        return mealDate;
    }
    
    public void setMealDate(String mealDate) {
        this.mealDate = mealDate;
    }
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
