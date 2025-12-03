package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.Recipe;
import ru.bmstu.iu6.culinarycompanion.domain.enums.RecipeCategory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeDAO {
    
    private final DataSource dataSource;
    
    public RecipeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Recipe create(Recipe recipe) throws SQLException {
        String sql = "INSERT INTO recipes (user_id, title, description, instructions, prep_time, cook_time, servings, calories, category, image_url, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipe.getUserId());
            stmt.setString(2, recipe.getTitle());
            stmt.setString(3, recipe.getDescription());
            stmt.setString(4, recipe.getInstructions());
            stmt.setInt(5, recipe.getPrepTime());
            stmt.setInt(6, recipe.getCookTime());
            stmt.setInt(7, recipe.getServings());
            stmt.setInt(8, recipe.getCalories());
            stmt.setString(9, recipe.getCategory().name());
            stmt.setString(10, recipe.getImageUrl());
            stmt.setTimestamp(11, Timestamp.valueOf(recipe.getCreatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                recipe.setId(rs.getLong("id"));
            }
            
            return recipe;
        }
    }
    
    public Optional<Recipe> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM recipes WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRecipe(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<Recipe> findAll() throws SQLException {
        String sql = "SELECT * FROM recipes ORDER BY created_at DESC";
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs));
            }
        }
        
        return recipes;
    }
    
    public List<Recipe> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM recipes WHERE user_id = ? ORDER BY created_at DESC";
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs));
            }
        }
        
        return recipes;
    }
    
    public List<Recipe> findByCategory(RecipeCategory category) throws SQLException {
        String sql = "SELECT * FROM recipes WHERE category = ? ORDER BY created_at DESC";
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs));
            }
        }
        
        return recipes;
    }
    
    public List<Recipe> search(String query) throws SQLException {
        String sql = "SELECT * FROM recipes WHERE LOWER(title) LIKE ? OR LOWER(description) LIKE ? ORDER BY created_at DESC";
        List<Recipe> recipes = new ArrayList<>();
        String searchPattern = "%" + query.toLowerCase() + "%";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs));
            }
        }
        
        return recipes;
    }
    
    public List<Recipe> searchByFilters(String query, RecipeCategory category) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM recipes WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (query != null && !query.isEmpty()) {
            sql.append(" AND (LOWER(title) LIKE ? OR LOWER(description) LIKE ?)");
            String searchPattern = "%" + query.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (category != null) {
            sql.append(" AND category = ?");
            params.add(category.name());
        }
        
        sql.append(" ORDER BY created_at DESC");
        
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs));
            }
        }
        
        return recipes;
    }
    
    public void update(Recipe recipe) throws SQLException {
        String sql = "UPDATE recipes SET title = ?, description = ?, instructions = ?, prep_time = ?, cook_time = ?, servings = ?, calories = ?, category = ?, image_url = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recipe.getTitle());
            stmt.setString(2, recipe.getDescription());
            stmt.setString(3, recipe.getInstructions());
            stmt.setInt(4, recipe.getPrepTime());
            stmt.setInt(5, recipe.getCookTime());
            stmt.setInt(6, recipe.getServings());
            stmt.setInt(7, recipe.getCalories());
            stmt.setString(8, recipe.getCategory().name());
            stmt.setString(9, recipe.getImageUrl());
            stmt.setLong(10, recipe.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM recipes WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public Double getAverageRating(Long recipeId) throws SQLException {
        String sql = "SELECT AVG(rating) as avg_rating FROM ratings WHERE recipe_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double avg = rs.getDouble("avg_rating");
                return rs.wasNull() ? 0.0 : avg;
            }
            
            return 0.0;
        }
    }
    
    private Recipe mapResultSetToRecipe(ResultSet rs) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setId(rs.getLong("id"));
        recipe.setUserId(rs.getLong("user_id"));
        recipe.setTitle(rs.getString("title"));
        recipe.setDescription(rs.getString("description"));
        recipe.setInstructions(rs.getString("instructions"));
        recipe.setPrepTime(rs.getInt("prep_time"));
        recipe.setCookTime(rs.getInt("cook_time"));
        recipe.setServings(rs.getInt("servings"));
        recipe.setCalories(rs.getInt("calories"));
        recipe.setCategory(RecipeCategory.valueOf(rs.getString("category")));
        recipe.setImageUrl(rs.getString("image_url"));
        recipe.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return recipe;
    }
}
