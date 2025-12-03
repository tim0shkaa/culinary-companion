package ru.bmstu.iu6.culinarycompanion.domain;

import ru.bmstu.iu6.culinarycompanion.domain.enums.MeasurementUnit;

public class RecipeIngredient {
    
    private Long id;
    private Long recipeId;
    private Long ingredientId;
    private Double quantity;
    private MeasurementUnit unit;
    private String ingredientName;
    
    public RecipeIngredient() {
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
    
    public Long getIngredientId() {
        return ingredientId;
    }
    
    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }
    
    public Double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public MeasurementUnit getUnit() {
        return unit;
    }
    
    public void setUnit(MeasurementUnit unit) {
        this.unit = unit;
    }
    
    public String getIngredientName() {
        return ingredientName;
    }
    
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
