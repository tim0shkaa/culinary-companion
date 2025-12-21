package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.domain.*;
import ru.bmstu.iu6.culinarycompanion.domain.enums.MealType;
import ru.bmstu.iu6.culinarycompanion.dto.request.UserMealEntryRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.DayNutritionResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserMealEntryResponse;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserMealPlanningService {

    private final UserMealEntryDAO userMealEntryDAO;
    private final RecipeDAO recipeDAO;
    private final RecipeIngredientDAO recipeIngredientDAO;
    private final IngredientDAO ingredientDAO;

    public UserMealPlanningService(UserMealEntryDAO userMealEntryDAO, RecipeDAO recipeDAO,
                                   RecipeIngredientDAO recipeIngredientDAO, IngredientDAO ingredientDAO) {
        this.userMealEntryDAO = userMealEntryDAO;
        this.recipeDAO = recipeDAO;
        this.recipeIngredientDAO = recipeIngredientDAO;
        this.ingredientDAO = ingredientDAO;
    }

    public List<UserMealEntryResponse> getWeekEntries(Long userId, String startDateStr) throws SQLException {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = startDate.plusDays(6);

        List<UserMealEntry> entries = userMealEntryDAO.findByUserIdAndDateRange(userId, startDate, endDate);
        List<UserMealEntryResponse> responses = new ArrayList<>();

        for (UserMealEntry entry : entries) {
            responses.add(mapToResponse(entry));
        }

        return responses;
    }

    public List<UserMealEntryResponse> getDayEntries(Long userId, String dateStr) throws SQLException {
        LocalDate date = LocalDate.parse(dateStr);
        List<UserMealEntry> entries = userMealEntryDAO.findByUserIdAndDate(userId, date);
        List<UserMealEntryResponse> responses = new ArrayList<>();

        for (UserMealEntry entry : entries) {
            responses.add(mapToResponse(entry));
        }

        return responses;
    }

    public UserMealEntry addEntry(Long userId, UserMealEntryRequest request)
            throws NotFoundException, SQLException {

        if (recipeDAO.findById(request.getRecipeId()).isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }

        UserMealEntry entry = new UserMealEntry();
        entry.setUserId(userId);
        entry.setRecipeId(request.getRecipeId());
        entry.setMealDate(LocalDate.parse(request.getMealDate()));
        entry.setMealType(MealType.valueOf(request.getMealType()));

        return userMealEntryDAO.create(entry);
    }

    public void deleteEntry(Long entryId) throws SQLException {
        userMealEntryDAO.delete(entryId);
    }

    public DayNutritionResponse getDayNutrition(Long userId, String dateStr) throws SQLException {
        LocalDate date = LocalDate.parse(dateStr);
        List<UserMealEntry> entries = userMealEntryDAO.findByUserIdAndDate(userId, date);

        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;
        int totalCalories = 0;

        for (UserMealEntry entry : entries) {
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

    private UserMealEntryResponse mapToResponse(UserMealEntry entry) {
        UserMealEntryResponse response = new UserMealEntryResponse();
        response.setId(entry.getId());
        response.setRecipeId(entry.getRecipeId());
        response.setRecipeTitle(entry.getRecipeTitle());
        response.setRecipeImageUrl(entry.getRecipeImageUrl());
        response.setMealDate(entry.getMealDate().toString());
        response.setMealType(entry.getMealType().name());
        return response;
    }

    
}
