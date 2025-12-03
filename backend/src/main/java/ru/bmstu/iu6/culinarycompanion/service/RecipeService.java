package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.domain.*;
import ru.bmstu.iu6.culinarycompanion.domain.enums.MeasurementUnit;
import ru.bmstu.iu6.culinarycompanion.domain.enums.RecipeCategory;
import ru.bmstu.iu6.culinarycompanion.dto.request.RecipeCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.request.RecipeUpdateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.RecipeDetailResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.RecipeResponse;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.util.ValidationUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeService {
    
    private final RecipeDAO recipeDAO;
    private final IngredientDAO ingredientDAO;
    private final RecipeIngredientDAO recipeIngredientDAO;
    private final UserDAO userDAO;
    private final RatingDAO ratingDAO;
    
    public RecipeService(RecipeDAO recipeDAO, IngredientDAO ingredientDAO, 
                        RecipeIngredientDAO recipeIngredientDAO, UserDAO userDAO, RatingDAO ratingDAO) {
        this.recipeDAO = recipeDAO;
        this.ingredientDAO = ingredientDAO;
        this.recipeIngredientDAO = recipeIngredientDAO;
        this.userDAO = userDAO;
        this.ratingDAO = ratingDAO;
    }
    
    public List<RecipeResponse> getAllRecipes(String search, String category) throws SQLException {
        List<Recipe> recipes;
        
        if (category != null && !category.isEmpty()) {
            RecipeCategory recipeCategory = RecipeCategory.valueOf(category.toUpperCase());
            
            if (search != null && !search.isEmpty()) {
                recipes = recipeDAO.searchByFilters(search, recipeCategory);
            } else {
                recipes = recipeDAO.findByCategory(recipeCategory);
            }
        } else if (search != null && !search.isEmpty()) {
            recipes = recipeDAO.search(search);
        } else {
            recipes = recipeDAO.findAll();
        }
        
        List<RecipeResponse> responses = new ArrayList<>();
        for (Recipe recipe : recipes) {
            responses.add(mapToRecipeResponse(recipe));
        }
        
        return responses;
    }
    
    public RecipeDetailResponse getRecipeById(Long recipeId) throws NotFoundException, SQLException {
        Optional<Recipe> recipeOpt = recipeDAO.findById(recipeId);
        
        if (recipeOpt.isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }
        
        Recipe recipe = recipeOpt.get();
        List<RecipeIngredient> ingredients = recipeIngredientDAO.findByRecipeId(recipeId);
        Double avgRating = ratingDAO.getAverageRating(recipeId);
        int ratingCount = ratingDAO.getRatingCount(recipeId);
        
        Optional<User> userOpt = userDAO.findById(recipe.getUserId());
        String username = userOpt.map(User::getUsername).orElse("Unknown");
        
        return mapToRecipeDetailResponse(recipe, ingredients, avgRating, ratingCount, username);
    }
    
    public RecipeDetailResponse createRecipe(Long userId, RecipeCreateRequest request) 
            throws ValidationException, SQLException {
        
        ValidationUtil.validateNotEmpty(request.getTitle(), "Title");
        ValidationUtil.validateNotEmpty(request.getDescription(), "Description");
        ValidationUtil.validateNotEmpty(request.getInstructions(), "Instructions");
        ValidationUtil.validatePositive(request.getPrepTime(), "Prep time");
        ValidationUtil.validatePositive(request.getCookTime(), "Cook time");
        ValidationUtil.validatePositive(request.getServings(), "Servings");
        
        Recipe recipe = new Recipe();
        recipe.setUserId(userId);
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setCookTime(request.getCookTime());
        recipe.setServings(request.getServings());
        recipe.setCalories(request.getCalories());
        recipe.setCategory(RecipeCategory.valueOf(request.getCategory().toUpperCase()));
        recipe.setImageUrl(request.getImageUrl());
        
        recipe = recipeDAO.create(recipe);
        
        List<RecipeIngredient> ingredients = new ArrayList<>();
        if (request.getIngredients() != null) {
            for (RecipeCreateRequest.IngredientRequest ingReq : request.getIngredients()) {
                Ingredient ingredient = ingredientDAO.findOrCreate(ingReq.getName());
                
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipeId(recipe.getId());
                recipeIngredient.setIngredientId(ingredient.getId());
                recipeIngredient.setQuantity(ingReq.getQuantity());
                recipeIngredient.setUnit(MeasurementUnit.valueOf(ingReq.getUnit().toUpperCase()));
                
                recipeIngredient = recipeIngredientDAO.create(recipeIngredient);
                recipeIngredient.setIngredientName(ingredient.getName());
                ingredients.add(recipeIngredient);
            }
        }
        
        Optional<User> userOpt = userDAO.findById(userId);
        String username = userOpt.map(User::getUsername).orElse("Unknown");
        
        return mapToRecipeDetailResponse(recipe, ingredients, 0.0, 0, username);
    }
    
    public RecipeDetailResponse updateRecipe(Long userId, Long recipeId, RecipeUpdateRequest request) 
            throws NotFoundException, ForbiddenException, ValidationException, SQLException {
        
        Optional<Recipe> recipeOpt = recipeDAO.findById(recipeId);
        
        if (recipeOpt.isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }
        
        Recipe recipe = recipeOpt.get();
        
        if (!recipe.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only update your own recipes");
        }
        
        ValidationUtil.validateNotEmpty(request.getTitle(), "Title");
        ValidationUtil.validateNotEmpty(request.getDescription(), "Description");
        ValidationUtil.validateNotEmpty(request.getInstructions(), "Instructions");
        ValidationUtil.validatePositive(request.getPrepTime(), "Prep time");
        ValidationUtil.validatePositive(request.getCookTime(), "Cook time");
        ValidationUtil.validatePositive(request.getServings(), "Servings");
        
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setCookTime(request.getCookTime());
        recipe.setServings(request.getServings());
        recipe.setCalories(request.getCalories());
        recipe.setCategory(RecipeCategory.valueOf(request.getCategory().toUpperCase()));
        recipe.setImageUrl(request.getImageUrl());
        
        recipeDAO.update(recipe);
        
        recipeIngredientDAO.deleteByRecipeId(recipeId);
        
        List<RecipeIngredient> ingredients = new ArrayList<>();
        if (request.getIngredients() != null) {
            for (RecipeUpdateRequest.IngredientRequest ingReq : request.getIngredients()) {
                Ingredient ingredient = ingredientDAO.findOrCreate(ingReq.getName());
                
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipeId(recipe.getId());
                recipeIngredient.setIngredientId(ingredient.getId());
                recipeIngredient.setQuantity(ingReq.getQuantity());
                recipeIngredient.setUnit(MeasurementUnit.valueOf(ingReq.getUnit().toUpperCase()));
                
                recipeIngredient = recipeIngredientDAO.create(recipeIngredient);
                recipeIngredient.setIngredientName(ingredient.getName());
                ingredients.add(recipeIngredient);
            }
        }
        
        Double avgRating = ratingDAO.getAverageRating(recipeId);
        int ratingCount = ratingDAO.getRatingCount(recipeId);
        
        Optional<User> userOpt = userDAO.findById(userId);
        String username = userOpt.map(User::getUsername).orElse("Unknown");
        
        return mapToRecipeDetailResponse(recipe, ingredients, avgRating, ratingCount, username);
    }
    
    public void deleteRecipe(Long userId, Long recipeId) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<Recipe> recipeOpt = recipeDAO.findById(recipeId);
        
        if (recipeOpt.isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }
        
        Recipe recipe = recipeOpt.get();
        
        if (!recipe.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own recipes");
        }
        
        recipeIngredientDAO.deleteByRecipeId(recipeId);
        ratingDAO.deleteByRecipeId(recipeId);
        recipeDAO.delete(recipeId);
    }
    
    public List<RecipeResponse> getRecipesByUserId(Long userId) throws SQLException {
        List<Recipe> recipes = recipeDAO.findByUserId(userId);
        List<RecipeResponse> responses = new ArrayList<>();
        
        for (Recipe recipe : recipes) {
            responses.add(mapToRecipeResponse(recipe));
        }
        
        return responses;
    }
    
    public void addRecipeToUser(Long userId, Long recipeId) throws NotFoundException, SQLException {
        Optional<Recipe> recipeOpt = recipeDAO.findById(recipeId);
        
        if (recipeOpt.isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }
    }
    
    private RecipeResponse mapToRecipeResponse(Recipe recipe) throws SQLException {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setUserId(recipe.getUserId());
        response.setTitle(recipe.getTitle());
        response.setDescription(recipe.getDescription());
        response.setPrepTime(recipe.getPrepTime());
        response.setCookTime(recipe.getCookTime());
        response.setServings(recipe.getServings());
        response.setCalories(recipe.getCalories());
        response.setCategory(recipe.getCategory().name());
        response.setImageUrl(recipe.getImageUrl());
        response.setAverageRating(ratingDAO.getAverageRating(recipe.getId()));
        response.setCreatedAt(recipe.getCreatedAt().toString());
        
        Optional<User> userOpt = userDAO.findById(recipe.getUserId());
        response.setUsername(userOpt.map(User::getUsername).orElse("Unknown"));
        
        return response;
    }
    
    private RecipeDetailResponse mapToRecipeDetailResponse(Recipe recipe, List<RecipeIngredient> ingredients, 
                                                           Double avgRating, int ratingCount, String username) {
        RecipeDetailResponse response = new RecipeDetailResponse();
        response.setId(recipe.getId());
        response.setUserId(recipe.getUserId());
        response.setUsername(username);
        response.setTitle(recipe.getTitle());
        response.setDescription(recipe.getDescription());
        response.setInstructions(recipe.getInstructions());
        response.setPrepTime(recipe.getPrepTime());
        response.setCookTime(recipe.getCookTime());
        response.setServings(recipe.getServings());
        response.setCalories(recipe.getCalories());
        response.setCategory(recipe.getCategory().name());
        response.setImageUrl(recipe.getImageUrl());
        response.setAverageRating(avgRating);
        response.setRatingCount(ratingCount);
        response.setCreatedAt(recipe.getCreatedAt().toString());
        
        List<RecipeDetailResponse.IngredientResponse> ingredientResponses = new ArrayList<>();
        for (RecipeIngredient ing : ingredients) {
            RecipeDetailResponse.IngredientResponse ingResp = new RecipeDetailResponse.IngredientResponse();
            ingResp.setId(ing.getId());
            ingResp.setIngredientId(ing.getIngredientId());
            ingResp.setName(ing.getIngredientName());
            ingResp.setQuantity(ing.getQuantity());
            ingResp.setUnit(ing.getUnit().name());
            ingredientResponses.add(ingResp);
        }
        response.setIngredients(ingredientResponses);
        
        return response;
    }
}
