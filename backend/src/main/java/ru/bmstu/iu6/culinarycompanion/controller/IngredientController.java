package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.service.IngredientService;
import ru.bmstu.iu6.culinarycompanion.domain.Ingredient;

import java.util.List;

public class IngredientController {
    
    private final IngredientService ingredientService;
    private final Gson gson;
    
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
        this.gson = new Gson();
    }
    
    public void searchIngredients(Context ctx) {
        try {
            String query = ctx.queryParam("q");
            List<Ingredient> ingredients = ingredientService.searchIngredients(query);
            ctx.status(200).json(ingredients);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getAllIngredients(Context ctx) {
        try {
            List<Ingredient> ingredients = ingredientService.getAllIngredients();
            ctx.status(200).json(ingredients);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
