package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.IngredientDAO;
import ru.bmstu.iu6.culinarycompanion.domain.Ingredient;

import java.sql.SQLException;
import java.util.List;

public class IngredientService {
    
    private final IngredientDAO ingredientDAO;
    
    public IngredientService(IngredientDAO ingredientDAO) {
        this.ingredientDAO = ingredientDAO;
    }
    
    public List<Ingredient> searchIngredients(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return ingredientDAO.findAll();
        }
        return ingredientDAO.search(query);
    }
    
    public List<Ingredient> getAllIngredients() throws SQLException {
        return ingredientDAO.findAll();
    }
}
