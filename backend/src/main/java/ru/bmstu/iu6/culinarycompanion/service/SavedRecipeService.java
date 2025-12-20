package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.RecipeDAO;
import ru.bmstu.iu6.culinarycompanion.dao.SavedRecipeDAO;
import ru.bmstu.iu6.culinarycompanion.domain.Recipe;
import ru.bmstu.iu6.culinarycompanion.dto.response.RecipeResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SavedRecipeService {
    
    private final SavedRecipeDAO savedRecipeDAO;
    private final RecipeDAO recipeDAO;
    
    public SavedRecipeService(SavedRecipeDAO savedRecipeDAO, RecipeDAO recipeDAO) {
        this.savedRecipeDAO = savedRecipeDAO;
        this.recipeDAO = recipeDAO;
    }
    
    public void saveRecipe(Long userId, Long recipeId) throws SQLException {
        savedRecipeDAO.saveRecipe(userId, recipeId);
    }
    
    public void unsaveRecipe(Long userId, Long recipeId) throws SQLException {
        savedRecipeDAO.unsaveRecipe(userId, recipeId);
    }
    
    public boolean isSaved(Long userId, Long recipeId) throws SQLException {
        return savedRecipeDAO.isSaved(userId, recipeId);
    }
    
    public List<RecipeResponse> getSavedRecipes(Long userId) throws SQLException {
        List<Long> recipeIds = savedRecipeDAO.getSavedRecipeIds(userId);
        List<RecipeResponse> recipes = new ArrayList<>();
        
        for (Long recipeId : recipeIds) {
            recipeDAO.findById(recipeId).ifPresent(recipe -> {
                try {
                    RecipeResponse response = mapToResponse(recipe);
                    recipes.add(response);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        
        return recipes;
    }

    private RecipeResponse mapToResponse(Recipe recipe) throws SQLException {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setUserId(recipe.getUserId());
        response.setTitle(recipe.getTitle());
        response.setDescription(recipe.getDescription());
        response.setPrepTime(recipe.getPrepTime());
        response.setCookTime(recipe.getCookTime());
        response.setServings(recipe.getServings());
        response.setImageUrl(recipe.getImageUrl());
        response.setCreatedAt(recipe.getCreatedAt().toString());

        Double avgRating = recipeDAO.getAverageRating(recipe.getId());
        response.setAverageRating(avgRating);

        return response;
    }
}
