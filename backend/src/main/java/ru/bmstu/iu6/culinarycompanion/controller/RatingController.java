package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.request.RatingRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.service.RatingService;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;

public class RatingController {
    
    private final RatingService ratingService;
    private final Gson gson;
    
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
        this.gson = new Gson();
    }
    
    public void rateRecipe(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            RatingRequest request = gson.fromJson(ctx.body(), RatingRequest.class);
            ratingService.rateRecipe(userId, recipeId, request);
            ctx.status(200).json(new ErrorResponse("Rating submitted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ValidationException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getRecipeRating(Context ctx) {
        try {
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            Double averageRating = ratingService.getAverageRating(recipeId);
            ctx.status(200).json(averageRating);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
