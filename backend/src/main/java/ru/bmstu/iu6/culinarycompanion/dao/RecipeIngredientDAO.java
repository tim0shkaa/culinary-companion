package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.RecipeIngredient;
import ru.bmstu.iu6.culinarycompanion.domain.enums.MeasurementUnit;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientDAO {
    
    private final DataSource dataSource;
    
    public RecipeIngredientDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public RecipeIngredient create(RecipeIngredient recipeIngredient) throws SQLException {
        String sql = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeIngredient.getRecipeId());
            stmt.setLong(2, recipeIngredient.getIngredientId());
            stmt.setDouble(3, recipeIngredient.getQuantity());
            stmt.setString(4, recipeIngredient.getUnit().name());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                recipeIngredient.setId(rs.getLong("id"));
            }
            
            return recipeIngredient;
        }
    }
    
    public List<RecipeIngredient> findByRecipeId(Long recipeId) throws SQLException {
        String sql = "SELECT ri.*, i.name as ingredient_name FROM recipe_ingredients ri " +
                     "JOIN ingredients i ON ri.ingredient_id = i.id WHERE ri.recipe_id = ?";
        List<RecipeIngredient> ingredients = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ingredients.add(mapResultSetToRecipeIngredient(rs));
            }
        }
        
        return ingredients;
    }
    
    public void deleteByRecipeId(Long recipeId) throws SQLException {
        String sql = "DELETE FROM recipe_ingredients WHERE recipe_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            stmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM recipe_ingredients WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    private RecipeIngredient mapResultSetToRecipeIngredient(ResultSet rs) throws SQLException {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setId(rs.getLong("id"));
        recipeIngredient.setRecipeId(rs.getLong("recipe_id"));
        recipeIngredient.setIngredientId(rs.getLong("ingredient_id"));
        recipeIngredient.setQuantity(rs.getDouble("quantity"));
        recipeIngredient.setUnit(MeasurementUnit.valueOf(rs.getString("unit")));
        recipeIngredient.setIngredientName(rs.getString("ingredient_name"));
        return recipeIngredient;
    }
}
