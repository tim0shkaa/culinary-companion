package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.IngredientDAO;
import ru.bmstu.iu6.culinarycompanion.dao.PantryDAO;
import ru.bmstu.iu6.culinarycompanion.domain.Ingredient;
import ru.bmstu.iu6.culinarycompanion.domain.PantryItem;

import java.sql.SQLException;
import java.util.List;

public class PantryService {
    
    private final PantryDAO pantryDAO;
    private final IngredientDAO ingredientDAO;
    
    public PantryService(PantryDAO pantryDAO, IngredientDAO ingredientDAO) {
        this.pantryDAO = pantryDAO;
        this.ingredientDAO = ingredientDAO;
    }
    
    public List<PantryItem> getUserPantry(Long userId) throws SQLException {
        return pantryDAO.findByUserId(userId);
    }
    
    public PantryItem addToPantry(Long userId, String ingredientName) throws SQLException {
        Ingredient ingredient = ingredientDAO.findOrCreate(ingredientName);
        
        if (pantryDAO.existsByUserIdAndIngredientId(userId, ingredient.getId())) {
            return pantryDAO.findByUserIdAndIngredientId(userId, ingredient.getId()).orElse(null);
        }
        
        PantryItem pantryItem = new PantryItem();
        pantryItem.setUserId(userId);
        pantryItem.setIngredientId(ingredient.getId());
        
        return pantryDAO.create(pantryItem);
    }
    
    public void removeFromPantry(Long userId, Long ingredientId) throws SQLException {
        pantryDAO.deleteByUserIdAndIngredientId(userId, ingredientId);
    }
}
