package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.request.RecipeCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.request.RecipeUpdateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.RecipeResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.RecipeDetailResponse;
import ru.bmstu.iu6.culinarycompanion.service.RecipeService;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;

import java.util.List;

public class RecipeController {
    
    private final RecipeService recipeService;
    private final Gson gson;
    
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
        this.gson = new Gson();
    }
    
    public void getAll(Context ctx) {
        try {
            String search = ctx.queryParam("search");
            String category = ctx.queryParam("category");
            List<RecipeResponse> recipes = recipeService.getAllRecipes(search, category);
            ctx.status(200).json(recipes);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getById(Context ctx) {
        try {
            Long recipeId = Long.parseLong(ctx.pathParam("id"));
            RecipeDetailResponse response = recipeService.getRecipeById(recipeId);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("Invalid recipe ID"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void create(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            RecipeCreateRequest request = gson.fromJson(ctx.body(), RecipeCreateRequest.class);
            RecipeDetailResponse response = recipeService.createRecipe(userId, request);
            ctx.status(201).json(response);
        } catch (ValidationException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void update(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("id"));
            RecipeUpdateRequest request = gson.fromJson(ctx.body(), RecipeUpdateRequest.class);
            RecipeDetailResponse response = recipeService.updateRecipe(userId, recipeId, request);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (ValidationException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void delete(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("id"));
            recipeService.deleteRecipe(userId, recipeId);
            ctx.status(200).json(new ErrorResponse("Recipe deleted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getUserRecipes(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            List<RecipeResponse> recipes = recipeService.getRecipesByUserId(userId);
            ctx.status(200).json(recipes);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void addToMyRecipes(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("id"));
            recipeService.addRecipeToUser(userId, recipeId);
            ctx.status(200).json(new ErrorResponse("Recipe added to your cookbook"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
