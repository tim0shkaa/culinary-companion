package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.domain.*;
import ru.bmstu.iu6.culinarycompanion.dto.response.ShoppingListResponse;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;

import java.sql.SQLException;
import java.util.*;

public class ShoppingListService {
    
    private final ShoppingListDAO shoppingListDAO;
    private final MealPlanDAO mealPlanDAO;
    private final MealPlanRecipeDAO mealPlanRecipeDAO;
    private final RecipeIngredientDAO recipeIngredientDAO;
    private final PantryDAO pantryDAO;
    
    public ShoppingListService(ShoppingListDAO shoppingListDAO, MealPlanDAO mealPlanDAO,
                              MealPlanRecipeDAO mealPlanRecipeDAO, RecipeIngredientDAO recipeIngredientDAO,
                              PantryDAO pantryDAO) {
        this.shoppingListDAO = shoppingListDAO;
        this.mealPlanDAO = mealPlanDAO;
        this.mealPlanRecipeDAO = mealPlanRecipeDAO;
        this.recipeIngredientDAO = recipeIngredientDAO;
        this.pantryDAO = pantryDAO;
    }
    
    public ShoppingListResponse generateFromMealPlan(Long userId, Long mealPlanId) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<MealPlan> mealPlanOpt = mealPlanDAO.findById(mealPlanId);
        
        if (mealPlanOpt.isEmpty()) {
            throw new NotFoundException("Meal plan not found");
        }
        
        MealPlan mealPlan = mealPlanOpt.get();
        
        if (!mealPlan.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only generate shopping lists for your own meal plans");
        }
        
        List<MealPlanRecipe> mealPlanRecipes = mealPlanRecipeDAO.findByMealPlanId(mealPlanId);
        
        Map<Long, RecipeIngredient> aggregatedIngredients = new HashMap<>();
        
        for (MealPlanRecipe mealPlanRecipe : mealPlanRecipes) {
            List<RecipeIngredient> ingredients = recipeIngredientDAO.findByRecipeId(mealPlanRecipe.getRecipeId());
            
            for (RecipeIngredient ingredient : ingredients) {
                Long ingredientId = ingredient.getIngredientId();
                
                if (aggregatedIngredients.containsKey(ingredientId)) {
                    RecipeIngredient existing = aggregatedIngredients.get(ingredientId);
                    existing.setQuantity(existing.getQuantity() + ingredient.getQuantity());
                } else {
                    aggregatedIngredients.put(ingredientId, ingredient);
                }
            }
        }
        
        List<PantryItem> pantryItems = pantryDAO.findByUserId(userId);
        Set<Long> pantryIngredientIds = new HashSet<>();
        for (PantryItem item : pantryItems) {
            pantryIngredientIds.add(item.getIngredientId());
        }
        
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setUserId(userId);
        shoppingList.setMealPlanId(mealPlanId);
        shoppingList = shoppingListDAO.create(shoppingList);
        
        List<ShoppingListResponse.ShoppingListItemResponse> items = new ArrayList<>();
        for (RecipeIngredient ingredient : aggregatedIngredients.values()) {
            if (!pantryIngredientIds.contains(ingredient.getIngredientId())) {
                ShoppingListResponse.ShoppingListItemResponse item = new ShoppingListResponse.ShoppingListItemResponse();
                item.setIngredientId(ingredient.getIngredientId());
                item.setIngredientName(ingredient.getIngredientName());
                item.setQuantity(ingredient.getQuantity());
                item.setUnit(ingredient.getUnit().name());
                item.setPurchased(false);
                items.add(item);
            }
        }
        
        ShoppingListResponse response = new ShoppingListResponse();
        response.setId(shoppingList.getId());
        response.setUserId(shoppingList.getUserId());
        response.setMealPlanId(shoppingList.getMealPlanId());
        response.setItems(items);
        response.setCreatedAt(shoppingList.getCreatedAt().toString());
        
        return response;
    }
    
    public ShoppingListResponse getShoppingListById(Long userId, Long shoppingListId) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<ShoppingList> shoppingListOpt = shoppingListDAO.findById(shoppingListId);
        
        if (shoppingListOpt.isEmpty()) {
            throw new NotFoundException("Shopping list not found");
        }
        
        ShoppingList shoppingList = shoppingListOpt.get();
        
        if (!shoppingList.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only access your own shopping lists");
        }
        
        ShoppingListResponse response = new ShoppingListResponse();
        response.setId(shoppingList.getId());
        response.setUserId(shoppingList.getUserId());
        response.setMealPlanId(shoppingList.getMealPlanId());
        response.setCreatedAt(shoppingList.getCreatedAt().toString());
        
        return response;
    }
    
    public ShoppingListResponse updateShoppingList(Long userId, Long shoppingListId, String body) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<ShoppingList> shoppingListOpt = shoppingListDAO.findById(shoppingListId);
        
        if (shoppingListOpt.isEmpty()) {
            throw new NotFoundException("Shopping list not found");
        }
        
        ShoppingList shoppingList = shoppingListOpt.get();
        
        if (!shoppingList.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only update your own shopping lists");
        }
        
        ShoppingListResponse response = new ShoppingListResponse();
        response.setId(shoppingList.getId());
        response.setUserId(shoppingList.getUserId());
        response.setMealPlanId(shoppingList.getMealPlanId());
        response.setCreatedAt(shoppingList.getCreatedAt().toString());
        
        return response;
    }
    
    public void deleteShoppingList(Long userId, Long shoppingListId) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<ShoppingList> shoppingListOpt = shoppingListDAO.findById(shoppingListId);
        
        if (shoppingListOpt.isEmpty()) {
            throw new NotFoundException("Shopping list not found");
        }
        
        ShoppingList shoppingList = shoppingListOpt.get();
        
        if (!shoppingList.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own shopping lists");
        }
        
        shoppingListDAO.delete(shoppingListId);
    }
}
