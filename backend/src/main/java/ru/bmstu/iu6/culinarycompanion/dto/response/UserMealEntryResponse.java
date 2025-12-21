package ru.bmstu.iu6.culinarycompanion.dto.response;

public class UserMealEntryResponse {
    
    private Long id;
    private Long recipeId;
    private String recipeTitle;
    private String recipeImageUrl;
    private String mealDate;
    private String mealType;
    
    public UserMealEntryResponse() {
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
    
    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }
    
    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
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
