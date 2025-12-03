package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.MealPlanDAO;
import ru.bmstu.iu6.culinarycompanion.dao.MealPlanRecipeDAO;
import ru.bmstu.iu6.culinarycompanion.domain.MealPlan;
import ru.bmstu.iu6.culinarycompanion.domain.MealPlanRecipe;
import ru.bmstu.iu6.culinarycompanion.dto.request.MealPlanCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.MealPlanResponse;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MealPlanService {
    
    private final MealPlanDAO mealPlanDAO;
    private final MealPlanRecipeDAO mealPlanRecipeDAO;
    
    public MealPlanService(MealPlanDAO mealPlanDAO, MealPlanRecipeDAO mealPlanRecipeDAO) {
        this.mealPlanDAO = mealPlanDAO;
        this.mealPlanRecipeDAO = mealPlanRecipeDAO;
    }
    
    public List<MealPlanResponse> getMealPlansByUserId(Long userId) throws SQLException {
        List<MealPlan> mealPlans = mealPlanDAO.findByUserId(userId);
        List<MealPlanResponse> responses = new ArrayList<>();
        
        for (MealPlan mealPlan : mealPlans) {
            List<MealPlanRecipe> recipes = mealPlanRecipeDAO.findByMealPlanId(mealPlan.getId());
            responses.add(mapToMealPlanResponse(mealPlan, recipes));
        }
        
        return responses;
    }
    
    public MealPlanResponse getMealPlanById(Long userId, Long mealPlanId) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<MealPlan> mealPlanOpt = mealPlanDAO.findById(mealPlanId);
        
        if (mealPlanOpt.isEmpty()) {
            throw new NotFoundException("Meal plan not found");
        }
        
        MealPlan mealPlan = mealPlanOpt.get();
        
        if (!mealPlan.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only access your own meal plans");
        }
        
        List<MealPlanRecipe> recipes = mealPlanRecipeDAO.findByMealPlanId(mealPlanId);
        
        return mapToMealPlanResponse(mealPlan, recipes);
    }
    
    public MealPlanResponse createMealPlan(Long userId, MealPlanCreateRequest request) 
            throws ValidationException, SQLException {
        
        ValidationUtil.validateNotEmpty(request.getName(), "Name");
        ValidationUtil.validateNotEmpty(request.getStartDate(), "Start date");
        ValidationUtil.validateNotEmpty(request.getEndDate(), "End date");
        
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUserId(userId);
        mealPlan.setName(request.getName());
        mealPlan.setStartDate(LocalDate.parse(request.getStartDate()));
        mealPlan.setEndDate(LocalDate.parse(request.getEndDate()));
        
        mealPlan = mealPlanDAO.create(mealPlan);
        
        List<MealPlanRecipe> recipes = new ArrayList<>();
        if (request.getRecipes() != null) {
            for (MealPlanCreateRequest.MealPlanRecipeRequest recipeReq : request.getRecipes()) {
                MealPlanRecipe mealPlanRecipe = new MealPlanRecipe();
                mealPlanRecipe.setMealPlanId(mealPlan.getId());
                mealPlanRecipe.setRecipeId(recipeReq.getRecipeId());
                mealPlanRecipe.setDate(LocalDate.parse(recipeReq.getDate()));
                mealPlanRecipe.setMealType(recipeReq.getMealType());
                
                mealPlanRecipe = mealPlanRecipeDAO.create(mealPlanRecipe);
                recipes.add(mealPlanRecipe);
            }
        }
        
        return mapToMealPlanResponse(mealPlan, recipes);
    }
    
    public void deleteMealPlan(Long userId, Long mealPlanId) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<MealPlan> mealPlanOpt = mealPlanDAO.findById(mealPlanId);
        
        if (mealPlanOpt.isEmpty()) {
            throw new NotFoundException("Meal plan not found");
        }
        
        MealPlan mealPlan = mealPlanOpt.get();
        
        if (!mealPlan.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own meal plans");
        }
        
        mealPlanRecipeDAO.deleteByMealPlanId(mealPlanId);
        mealPlanDAO.delete(mealPlanId);
    }
    
    private MealPlanResponse mapToMealPlanResponse(MealPlan mealPlan, List<MealPlanRecipe> recipes) {
        MealPlanResponse response = new MealPlanResponse();
        response.setId(mealPlan.getId());
        response.setUserId(mealPlan.getUserId());
        response.setName(mealPlan.getName());
        response.setStartDate(mealPlan.getStartDate().toString());
        response.setEndDate(mealPlan.getEndDate().toString());
        response.setCreatedAt(mealPlan.getCreatedAt().toString());
        
        List<MealPlanResponse.MealPlanRecipeResponse> recipeResponses = new ArrayList<>();
        for (MealPlanRecipe recipe : recipes) {
            MealPlanResponse.MealPlanRecipeResponse recipeResp = new MealPlanResponse.MealPlanRecipeResponse();
            recipeResp.setId(recipe.getId());
            recipeResp.setRecipeId(recipe.getRecipeId());
            recipeResp.setRecipeTitle(recipe.getRecipeTitle());
            recipeResp.setDate(recipe.getDate().toString());
            recipeResp.setMealType(recipe.getMealType());
            recipeResponses.add(recipeResp);
        }
        response.setRecipes(recipeResponses);
        
        return response;
    }
}
