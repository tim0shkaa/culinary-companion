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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // ДОБАВЬ ЭТИ МЕТОДЫ В КОНЕЦ UserMealPlanningService.java (перед закрывающей скобкой класса)

    public List<ShoppingListItemResponse> generateShoppingList(Long userId, String dateStr) throws SQLException {
        LocalDate date = LocalDate.parse(dateStr);
        List<UserMealEntry> entries = userMealEntryDAO.findByUserIdAndDate(userId, date);

        // Map для группировки ингредиентов: ingredientId -> количество
        Map<Long, Double> ingredientQuantities = new HashMap<>();
        Map<Long, String> ingredientNames = new HashMap<>();
        Map<Long, String> ingredientUnits = new HashMap<>();

        // Собираем все ингредиенты из всех рецептов дня
        for (UserMealEntry entry : entries) {
            List<RecipeIngredient> ingredients = recipeIngredientDAO.findByRecipeId(entry.getRecipeId());

            for (RecipeIngredient recipeIng : ingredients) {
                Long ingredientId = recipeIng.getIngredientId();

                // Суммируем количество
                ingredientQuantities.merge(ingredientId, recipeIng.getQuantity(), Double::sum);

                // Сохраняем название и единицу измерения
                if (!ingredientNames.containsKey(ingredientId)) {
                    Ingredient ing = ingredientDAO.findById(ingredientId).orElse(null);
                    if (ing != null) {
                        ingredientNames.put(ingredientId, ing.getName());
                        ingredientUnits.put(ingredientId, recipeIng.getUnit().name());
                    }
                }
            }
        }

        // Преобразуем в список ответов
        List<ShoppingListItemResponse> items = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : ingredientQuantities.entrySet()) {
            Long ingredientId = entry.getKey();
            ShoppingListItemResponse item = new ShoppingListItemResponse(
                    ingredientId,
                    ingredientNames.get(ingredientId),
                    entry.getValue(),
                    ingredientUnits.get(ingredientId)
            );
            items.add(item);
        }

        // Сортируем по имени
        items.sort((a, b) -> a.getIngredientName().compareTo(b.getIngredientName()));

        return items;
    }

    // Вспомогательный класс для ответа
    public static class ShoppingListItemResponse {
        private Long ingredientId;
        private String ingredientName;
        private Double quantity;
        private String unit;

        public ShoppingListItemResponse(Long ingredientId, String ingredientName, Double quantity, String unit) {
            this.ingredientId = ingredientId;
            this.ingredientName = ingredientName;
            this.quantity = quantity;
            this.unit = unit;
        }

        public Long getIngredientId() {
            return ingredientId;
        }

        public String getIngredientName() {
            return ingredientName;
        }

        public Double getQuantity() {
            return quantity;
        }

        public String getUnit() {
            return unit;
        }
    }
}
