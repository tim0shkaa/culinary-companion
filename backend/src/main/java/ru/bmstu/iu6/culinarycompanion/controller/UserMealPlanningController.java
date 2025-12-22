package ru.bmstu.iu6.culinarycompanion.controller;

import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.domain.UserMealEntry;
import ru.bmstu.iu6.culinarycompanion.dto.request.UserMealEntryRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.DayNutritionResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserMealEntryResponse;
import ru.bmstu.iu6.culinarycompanion.service.UserMealPlanningService;

import java.util.List;

public class UserMealPlanningController {

    private final UserMealPlanningService mealPlanningService;

    public UserMealPlanningController(UserMealPlanningService mealPlanningService) {
        this.mealPlanningService = mealPlanningService;
    }

    public void getWeekEntries(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");  // Извлекаем userId из middleware
            String startDate = ctx.queryParam("startDate");

            if (startDate == null || startDate.isEmpty()) {
                ctx.status(400).json(new ErrorResponse("startDate parameter is required"));
                return;
            }

            List<UserMealEntryResponse> entries = mealPlanningService.getWeekEntries(userId, startDate);
            ctx.json(entries);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Failed to get week entries: " + e.getMessage()));
        }
    }

    public void getDayEntries(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            String date = ctx.queryParam("date");

            if (date == null || date.isEmpty()) {
                ctx.status(400).json(new ErrorResponse("date parameter is required"));
                return;
            }

            List<UserMealEntryResponse> entries = mealPlanningService.getDayEntries(userId, date);
            ctx.json(entries);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Failed to get day entries: " + e.getMessage()));
        }
    }

    public void addEntry(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            UserMealEntryRequest request = ctx.bodyAsClass(UserMealEntryRequest.class);

            UserMealEntry entry = mealPlanningService.addEntry(userId, request);
            ctx.status(201).json(entry);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Failed to add entry: " + e.getMessage()));
        }
    }

    public void deleteEntry(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long entryId = Long.parseLong(ctx.pathParam("entryId"));

            mealPlanningService.deleteEntry(entryId);
            ctx.status(204);
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("Invalid entry ID"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Failed to delete entry: " + e.getMessage()));
        }
    }

    public void getDayNutrition(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            String date = ctx.queryParam("date");

            if (date == null || date.isEmpty()) {
                ctx.status(400).json(new ErrorResponse("date parameter is required"));
                return;
            }

            DayNutritionResponse nutrition = mealPlanningService.getDayNutrition(userId, date);
            ctx.json(nutrition);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Failed to get nutrition: " + e.getMessage()));
        }
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    public void generateShoppingList(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            String date = ctx.queryParam("date");

            if (date == null || date.isEmpty()) {
                ctx.status(400).json(new ErrorResponse("date parameter is required"));
                return;
            }

            List<UserMealPlanningService.ShoppingListItemResponse> items =
                    mealPlanningService.generateShoppingList(userId, date);

            ctx.json(items);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Failed to generate shopping list: " + e.getMessage()));
        }
    }
}