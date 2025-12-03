package ru.bmstu.iu6.culinarycompanion.dto.request;

import java.util.List;

public class RecipeCreateRequest {
    
    private String title;
    private String description;
    private String instructions;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private Integer calories;
    private String category;
    private String imageUrl;
    private List<IngredientRequest> ingredients;
    
    public RecipeCreateRequest() {
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
    
    public List<IngredientRequest> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<IngredientRequest> ingredients) {
        this.ingredients = ingredients;
    }
    
    public static class IngredientRequest {
        private String name;
        private Double quantity;
        private String unit;
        
        public IngredientRequest() {
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
