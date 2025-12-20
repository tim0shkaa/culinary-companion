package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.domain.*;
import ru.bmstu.iu6.culinarycompanion.domain.enums.MealType;
import ru.bmstu.iu6.culinarycompanion.dto.request.MealPlanCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.request.MealPlanEntryRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.DayNutritionResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.MealPlanEntryResponse;
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
    private final MealPlanEntryDAO mealPlanEntryDAO;
    private final RecipeDAO recipeDAO;
    private final RecipeIngredientDAO recipeIngredientDAO;
    private final IngredientDAO ingredientDAO;

    public MealPlanService(MealPlanDAO mealPlanDAO, MealPlanEntryDAO mealPlanEntryDAO,
                           RecipeDAO recipeDAO, RecipeIngredientDAO recipeIngredientDAO,
                           IngredientDAO ingredientDAO) {
        this.mealPlanDAO = mealPlanDAO;
        this.mealPlanEntryDAO = mealPlanEntryDAO;
        this.recipeDAO = recipeDAO;
        this.recipeIngredientDAO = recipeIngredientDAO;
        this.ingredientDAO = ingredientDAO;
    }

    public List<MealPlanResponse> getUserMealPlans(Long userId) throws SQLException {
        List<MealPlan> mealPlans = mealPlanDAO.findByUserId(userId);
        List<MealPlanResponse> responses = new ArrayList<>();

        for (MealPlan mealPlan : mealPlans) {
            responses.add(mapToResponse(mealPlan));
        }

        return responses;
    }

    public MealPlanResponse getMealPlanById(Long mealPlanId) throws NotFoundException, SQLException {
        Optional<MealPlan> mealPlanOpt = mealPlanDAO.findById(mealPlanId);

        if (mealPlanOpt.isEmpty()) {
            throw new NotFoundException("Meal plan not found");
        }

        return mapToResponse(mealPlanOpt.get());
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

        return mapToResponse(mealPlan);
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

        mealPlanEntryDAO.deleteByMealPlanId(mealPlanId);
        mealPlanDAO.delete(mealPlanId);
    }

    public MealPlanEntry addEntry(Long mealPlanId, MealPlanEntryRequest request)
            throws NotFoundException, SQLException {

        if (mealPlanDAO.findById(mealPlanId).isEmpty()) {
            throw new NotFoundException("Meal plan not found");
        }

        if (recipeDAO.findById(request.getRecipeId()).isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }

        MealPlanEntry entry = new MealPlanEntry();
        entry.setMealPlanId(mealPlanId);
        entry.setRecipeId(request.getRecipeId());
        entry.setMealDate(LocalDate.parse(request.getMealDate()));
        entry.setMealType(MealType.valueOf(request.getMealType()));

        return mealPlanEntryDAO.create(entry);
    }

    public void deleteEntry(Long entryId) throws SQLException {
        mealPlanEntryDAO.delete(entryId);
    }

    public List<MealPlanEntryResponse> getEntries(Long mealPlanId) throws SQLException {
        List<MealPlanEntry> entries = mealPlanEntryDAO.findByMealPlanId(mealPlanId);
        List<MealPlanEntryResponse> responses = new ArrayList<>();

        for (MealPlanEntry entry : entries) {
            responses.add(mapEntryToResponse(entry));
        }

        return responses;
    }

    public DayNutritionResponse getDayNutrition(Long mealPlanId, String dateStr) throws SQLException {
        LocalDate date = LocalDate.parse(dateStr);
        List<MealPlanEntry> entries = mealPlanEntryDAO.findByMealPlanIdAndDate(mealPlanId, date);

        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;
        int totalCalories = 0;

        for (MealPlanEntry entry : entries) {
            Recipe recipe = recipeDAO.findById(entry.getRecipeId()).orElse(null);
            if (recipe == null) continue;

            List<RecipeIngredient> ingredients = recipeIngredientDAO.findByRecipeId(recipe.getId());

            for (RecipeIngredient recipeIng : ingredients) {
                Ingredient ing = ingredientDAO.findById(recipeIng.getIngredientId()).orElse(null);
                if (ing == null) continue;

                double quantityIn100g = recipeIng.getQuantity() / 100.0;

                totalProteins += (ing.getProteins() != null ? ing.getProteins() : 0) * quantityIn100g;
                totalFats += (ing.getFats() != null ? ing.getFats() : 0) * quantityIn100g;
                totalCarbs += (ing.getCarbohydrates() != null ? ing.getCarbohydrates() : 0) * quantityIn100g;
                totalCalories += (ing.getCalories() != null ? ing.getCalories() : 0) * quantityIn100g;
            }
        }

        return new DayNutritionResponse(
                dateStr,
                Math.round(totalProteins * 10.0) / 10.0,
                Math.round(totalFats * 10.0) / 10.0,
                Math.round(totalCarbs * 10.0) / 10.0,
                (int) Math.round(totalCalories)
        );
    }

    private MealPlanResponse mapToResponse(MealPlan mealPlan) {
        MealPlanResponse response = new MealPlanResponse();
        response.setId(mealPlan.getId());
        response.setUserId(mealPlan.getUserId());
        response.setName(mealPlan.getName());
        response.setStartDate(mealPlan.getStartDate().toString());
        response.setEndDate(mealPlan.getEndDate().toString());
        response.setCreatedAt(mealPlan.getCreatedAt().toString());
        return response;
    }

    private MealPlanEntryResponse mapEntryToResponse(MealPlanEntry entry) {
        MealPlanEntryResponse response = new MealPlanEntryResponse();
        response.setId(entry.getId());
        response.setRecipeId(entry.getRecipeId());
        response.setRecipeTitle(entry.getRecipeTitle());
        response.setRecipeImageUrl(entry.getRecipeImageUrl());
        response.setMealDate(entry.getMealDate().toString());
        response.setMealType(entry.getMealType().name());
        return response;
    }
}