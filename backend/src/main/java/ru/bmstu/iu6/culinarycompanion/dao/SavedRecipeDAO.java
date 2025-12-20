package ru.bmstu.iu6.culinarycompanion.dao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavedRecipeDAO {
    
    private final DataSource dataSource;
    
    public SavedRecipeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void saveRecipe(Long userId, Long recipeId) throws SQLException {
        String sql = "INSERT INTO user_saved_recipes (user_id, recipe_id) VALUES (?, ?) ON CONFLICT (user_id, recipe_id) DO NOTHING";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, recipeId);
            stmt.executeUpdate();
        }
    }
    
    public void unsaveRecipe(Long userId, Long recipeId) throws SQLException {
        String sql = "DELETE FROM user_saved_recipes WHERE user_id = ? AND recipe_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, recipeId);
            stmt.executeUpdate();
        }
    }
    
    public boolean isSaved(Long userId, Long recipeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_saved_recipes WHERE user_id = ? AND recipe_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, recipeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
        }
    }
    
    public List<Long> getSavedRecipeIds(Long userId) throws SQLException {
        String sql = "SELECT recipe_id FROM user_saved_recipes WHERE user_id = ? ORDER BY saved_at DESC";
        List<Long> recipeIds = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                recipeIds.add(rs.getLong("recipe_id"));
            }
        }
        
        return recipeIds;
    }
    
    public int getSavedCount(Long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_saved_recipes WHERE user_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
        }
    }
}
