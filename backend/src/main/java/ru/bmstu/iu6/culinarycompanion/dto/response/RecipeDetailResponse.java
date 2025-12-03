package ru.bmstu.iu6.culinarycompanion.dto.response;

import java.util.List;

public class RecipeDetailResponse {
    
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private String instructions;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private Integer calories;
    private String category;
    private String imageUrl;
    private Double averageRating;
    private Integer ratingCount;
    private List<IngredientResponse> ingredients;
    private String createdAt;
    
    public RecipeDetailResponse() {
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public Integer getPrepTime() {
        return prepTime;
    }
    
    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }
    
    public Integer getCookTime() {
        return cookTime;
    }
    
    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }
    
    public Integer getServings() {
        return servings;
    }
    
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    
    public Integer getCalories() {
        return calories;
    }
    
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getRatingCount() {
        return ratingCount;
    }
    
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    public List<IngredientResponse> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<IngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public static class IngredientResponse {
        private Long id;
        private Long ingredientId;
        private String name;
        private Double quantity;
        private String unit;
        
        public IngredientResponse() {
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getIngredientId() {
            return ingredientId;
        }
        
        public void setIngredientId(Long ingredientId) {
            this.ingredientId = ingredientId;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Double getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }
        
        public String getUnit() {
            return unit;
        }
        
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
