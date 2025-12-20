package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.RecipeDAO;
import ru.bmstu.iu6.culinarycompanion.dao.SavedRecipeDAO;
import ru.bmstu.iu6.culinarycompanion.dao.UserDAO;
import ru.bmstu.iu6.culinarycompanion.domain.Recipe;
import ru.bmstu.iu6.culinarycompanion.domain.User;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserStatsResponse;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO;
    private final RecipeDAO recipeDAO;
    private final SavedRecipeDAO savedRecipeDAO;

    public UserService(UserDAO userDAO, RecipeDAO recipeDAO, SavedRecipeDAO savedRecipeDAO) {
        this.userDAO = userDAO;
        this.recipeDAO = recipeDAO;
        this.savedRecipeDAO = savedRecipeDAO;
    }

    public UserResponse getUserById(Long userId) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return mapToUserResponse(userOpt.get());
    }

    public UserStatsResponse getUserStats(Long userId) throws SQLException {
        List<Recipe> userRecipes = recipeDAO.findByUserId(userId);
        int recipesCount = userRecipes.size();

        double totalRating = 0;
        int ratedRecipes = 0;

        for (Recipe recipe : userRecipes) {
            Double avgRating = recipeDAO.getAverageRating(recipe.getId());
            if (avgRating != null && avgRating > 0) {
                totalRating += avgRating;
                ratedRecipes++;
            }
        }

        Double averageRating = ratedRecipes > 0 ? totalRating / ratedRecipes : 0.0;
        int savedCount = savedRecipeDAO.getSavedCount(userId);

        return new UserStatsResponse(recipesCount, savedCount, averageRating);
    }

    public UserResponse updateUser(Long userId, String body) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = userOpt.get();
        userDAO.update(user);

        return mapToUserResponse(user);
    }

    public void deleteUser(Long userId) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        userDAO.delete(userId);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setCreatedAt(user.getCreatedAt().toString());
        return response;
    }
}