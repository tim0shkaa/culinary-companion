package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.RatingDAO;
import ru.bmstu.iu6.culinarycompanion.dao.RecipeDAO;
import ru.bmstu.iu6.culinarycompanion.domain.Rating;
import ru.bmstu.iu6.culinarycompanion.dto.request.RatingRequest;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Optional;

public class RatingService {
    
    private final RatingDAO ratingDAO;
    private final RecipeDAO recipeDAO;
    
    public RatingService(RatingDAO ratingDAO, RecipeDAO recipeDAO) {
        this.ratingDAO = ratingDAO;
        this.recipeDAO = recipeDAO;
    }
    
    public void rateRecipe(Long userId, Long recipeId, RatingRequest request) 
            throws NotFoundException, ValidationException, SQLException {
        
        ValidationUtil.validateRating(request.getRating());
        
        if (recipeDAO.findById(recipeId).isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }
        
        Optional<Rating> existingRatingOpt = ratingDAO.findByRecipeIdAndUserId(recipeId, userId);
        
        if (existingRatingOpt.isPresent()) {
            Rating existingRating = existingRatingOpt.get();
            existingRating.setRating(request.getRating());
            ratingDAO.update(existingRating);
        } else {
            Rating rating = new Rating();
            rating.setRecipeId(recipeId);
            rating.setUserId(userId);
            rating.setRating(request.getRating());
            ratingDAO.create(rating);
        }
    }
    
    public Double getAverageRating(Long recipeId) throws SQLException {
        return ratingDAO.getAverageRating(recipeId);
    }
}
