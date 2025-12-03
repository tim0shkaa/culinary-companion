package ru.bmstu.iu6.culinarycompanion.domain;

import ru.bmstu.iu6.culinarycompanion.domain.enums.MeasurementUnit;

public class ShoppingListItem {
    
    private Long id;
    private Long shoppingListId;
    private Long ingredientId;
    private String ingredientName;
    private Double quantity;
    private MeasurementUnit unit;
    private Boolean purchased;
    
    public ShoppingListItem() {
        this.purchased = false;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getShoppingListId() {
        return shoppingListId;
    }
    
    public void setShoppingListId(Long shoppingListId) {
        this.shoppingListId = shoppingListId;
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
    
    public Boolean getPurchased() {
        return purchased;
    }
    
    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }
}
