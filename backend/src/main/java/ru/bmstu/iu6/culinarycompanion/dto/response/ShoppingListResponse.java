package ru.bmstu.iu6.culinarycompanion.dto.response;

import java.util.List;

public class ShoppingListResponse {
    
    private Long id;
    private Long userId;
    private Long mealPlanId;
    private List<ShoppingListItemResponse> items;
    private String createdAt;
    
    public ShoppingListResponse() {
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
    
    public Long getMealPlanId() {
        return mealPlanId;
    }
    
    public void setMealPlanId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
    }
    
    public List<ShoppingListItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<ShoppingListItemResponse> items) {
        this.items = items;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public static class ShoppingListItemResponse {
        private Long id;
        private Long ingredientId;
        private String ingredientName;
        private Double quantity;
        private String unit;
        private Boolean purchased;
        
        public ShoppingListItemResponse() {
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
        
        public String getUnit() {
            return unit;
        }
        
        public void setUnit(String unit) {
            this.unit = unit;
        }
        
        public Boolean getPurchased() {
            return purchased;
        }
        
        public void setPurchased(Boolean purchased) {
            this.purchased = purchased;
        }
    }
}
