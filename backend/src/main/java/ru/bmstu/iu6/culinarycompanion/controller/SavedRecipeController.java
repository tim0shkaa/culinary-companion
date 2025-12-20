package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.service.SavedRecipeService;

public class SavedRecipeController {
    
    private final SavedRecipeService savedRecipeService;
    private final Gson gson;
    
    public SavedRecipeController(SavedRecipeService savedRecipeService) {
        this.savedRecipeService = savedRecipeService;
        this.gson = new Gson();
    }
    
    public void saveRecipe(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            
            savedRecipeService.saveRecipe(userId, recipeId);
            ctx.status(200).json(new ErrorResponse("Recipe saved successfully"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void unsaveRecipe(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            
            savedRecipeService.unsaveRecipe(userId, recipeId);
            ctx.status(200).json(new ErrorResponse("Recipe removed from saved"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void isSaved(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            
            boolean saved = savedRecipeService.isSaved(userId, recipeId);
            ctx.status(200).json(new SavedStatusResponse(saved));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getSavedRecipes(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            
            var recipes = savedRecipeService.getSavedRecipes(userId);
            ctx.status(200).json(recipes);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    private static class SavedStatusResponse {
        private boolean saved;
        
        public SavedStatusResponse(boolean saved) {
            this.saved = saved;
        }
        
        public boolean isSaved() {
            return saved;
        }
    }
}
