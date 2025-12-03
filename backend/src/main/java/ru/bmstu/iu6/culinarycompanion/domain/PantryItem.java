package ru.bmstu.iu6.culinarycompanion.domain;

public class PantryItem {
    
    private Long id;
    private Long userId;
    private Long ingredientId;
    private String ingredientName;
    
    public PantryItem() {
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
    
    public Long getIngredientId() {
        return ingredientId;
    }
    
    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }
    
    public String getIngredientName() {
        return ingredientName;
    }
    
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
