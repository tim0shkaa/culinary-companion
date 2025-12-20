package ru.bmstu.iu6.culinarycompanion.dto.response;

public class UserStatsResponse {
    
    private int recipesCount;
    private int savedRecipesCount;
    private Double averageRating;
    
    public UserStatsResponse() {
    }
    
    public UserStatsResponse(int recipesCount, int savedRecipesCount, Double averageRating) {
        this.recipesCount = recipesCount;
        this.savedRecipesCount = savedRecipesCount;
        this.averageRating = averageRating;
    }
    
    public int getRecipesCount() {
        return recipesCount;
    }
    
    public void setRecipesCount(int recipesCount) {
        this.recipesCount = recipesCount;
    }
    
    public int getSavedRecipesCount() {
        return savedRecipesCount;
    }
    
    public void setSavedRecipesCount(int savedRecipesCount) {
        this.savedRecipesCount = savedRecipesCount;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
