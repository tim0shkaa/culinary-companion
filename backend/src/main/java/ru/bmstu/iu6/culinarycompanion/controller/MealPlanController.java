package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.domain.MealPlan;
import ru.bmstu.iu6.culinarycompanion.dto.request.MealPlanCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.request.MealPlanEntryRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.MealPlanResponse;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.service.MealPlanService;

import java.util.List;

public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final Gson gson;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
        this.gson = new Gson();
    }

    public void getUserMealPlans(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            List<MealPlanResponse> mealPlans = mealPlanService.getUserMealPlans(userId);
            ctx.status(200).json(mealPlans);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void getMealPlanById(Context ctx) {
        try {
            Long mealPlanId = Long.parseLong(ctx.pathParam("id"));
            MealPlanResponse mealPlan = mealPlanService.getMealPlanById(mealPlanId);
            ctx.status(200).json(mealPlan);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void create(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            MealPlanCreateRequest request = gson.fromJson(ctx.body(), MealPlanCreateRequest.class);
            MealPlanResponse mealPlan = mealPlanService.createMealPlan(userId, request);
            ctx.status(201).json(mealPlan);
        } catch (ValidationException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void delete(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long mealPlanId = Long.parseLong(ctx.pathParam("id"));
            mealPlanService.deleteMealPlan(userId, mealPlanId);
            ctx.status(200).json(new ErrorResponse("Meal plan deleted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void addEntry(Context ctx) {
        try {
            Long mealPlanId = Long.parseLong(ctx.pathParam("id"));
            MealPlanEntryRequest request = gson.fromJson(ctx.body(), MealPlanEntryRequest.class);

            var entry = mealPlanService.addEntry(mealPlanId, request);
            ctx.status(201).json(entry);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void deleteEntry(Context ctx) {
        try {
            Long entryId = Long.parseLong(ctx.pathParam("entryId"));

            mealPlanService.deleteEntry(entryId);
            ctx.status(200).json(new ErrorResponse("Entry deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void getEntries(Context ctx) {
        try {
            Long mealPlanId = Long.parseLong(ctx.pathParam("id"));

            var entries = mealPlanService.getEntries(mealPlanId);
            ctx.status(200).json(entries);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void getDayNutrition(Context ctx) {
        try {
            Long mealPlanId = Long.parseLong(ctx.pathParam("id"));
            String date = ctx.queryParam("date");

            if (date == null) {
                ctx.status(400).json(new ErrorResponse("Date parameter is required"));
                return;
            }

            var nutrition = mealPlanService.getDayNutrition(mealPlanId, date);
            ctx.status(200).json(nutrition);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}