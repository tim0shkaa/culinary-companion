package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.request.MealPlanCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.MealPlanResponse;
import ru.bmstu.iu6.culinarycompanion.service.MealPlanService;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;

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
            List<MealPlanResponse> mealPlans = mealPlanService.getMealPlansByUserId(userId);
            ctx.status(200).json(mealPlans);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getMealPlanById(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long mealPlanId = Long.parseLong(ctx.pathParam("id"));
            MealPlanResponse response = mealPlanService.getMealPlanById(userId, mealPlanId);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void create(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            MealPlanCreateRequest request = gson.fromJson(ctx.body(), MealPlanCreateRequest.class);
            MealPlanResponse response = mealPlanService.createMealPlan(userId, request);
            ctx.status(201).json(response);
        } catch (ValidationException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
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
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
