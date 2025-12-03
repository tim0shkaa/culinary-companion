package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.ShoppingListResponse;
import ru.bmstu.iu6.culinarycompanion.service.ShoppingListService;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;

public class ShoppingListController {
    
    private final ShoppingListService shoppingListService;
    private final Gson gson;
    
    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
        this.gson = new Gson();
    }
    
    public void generateFromMealPlan(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long mealPlanId = Long.parseLong(ctx.pathParam("mealPlanId"));
            ShoppingListResponse response = shoppingListService.generateFromMealPlan(userId, mealPlanId);
            ctx.status(201).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getShoppingList(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long shoppingListId = Long.parseLong(ctx.pathParam("id"));
            ShoppingListResponse response = shoppingListService.getShoppingListById(userId, shoppingListId);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void updateShoppingList(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long shoppingListId = Long.parseLong(ctx.pathParam("id"));
            String body = ctx.body();
            ShoppingListResponse response = shoppingListService.updateShoppingList(userId, shoppingListId, body);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void deleteShoppingList(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long shoppingListId = Long.parseLong(ctx.pathParam("id"));
            shoppingListService.deleteShoppingList(userId, shoppingListId);
            ctx.status(200).json(new ErrorResponse("Shopping list deleted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
