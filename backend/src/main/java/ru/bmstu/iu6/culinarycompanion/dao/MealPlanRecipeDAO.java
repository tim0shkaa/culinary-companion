package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.MealPlanRecipe;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MealPlanRecipeDAO {
    
    private final DataSource dataSource;
    
    public MealPlanRecipeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public MealPlanRecipe create(MealPlanRecipe mealPlanRecipe) throws SQLException {
        String sql = "INSERT INTO meal_plan_recipes (meal_plan_id, recipe_id, date, meal_type) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlanRecipe.getMealPlanId());
            stmt.setLong(2, mealPlanRecipe.getRecipeId());
            stmt.setDate(3, Date.valueOf(mealPlanRecipe.getDate()));
            stmt.setString(4, mealPlanRecipe.getMealType());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                mealPlanRecipe.setId(rs.getLong("id"));
            }
            
            return mealPlanRecipe;
        }
    }
    
    public List<MealPlanRecipe> findByMealPlanId(Long mealPlanId) throws SQLException {
        String sql = "SELECT mpr.*, r.title as recipe_title FROM meal_plan_recipes mpr " +
                     "JOIN recipes r ON mpr.recipe_id = r.id WHERE mpr.meal_plan_id = ? ORDER BY mpr.date, mpr.meal_type";
        List<MealPlanRecipe> recipes = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlanId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                recipes.add(mapResultSetToMealPlanRecipe(rs));
            }
        }
        
        return recipes;
    }
    
    public void deleteByMealPlanId(Long mealPlanId) throws SQLException {
        String sql = "DELETE FROM meal_plan_recipes WHERE meal_plan_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlanId);
            stmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM meal_plan_recipes WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    private MealPlanRecipe mapResultSetToMealPlanRecipe(ResultSet rs) throws SQLException {
        MealPlanRecipe mealPlanRecipe = new MealPlanRecipe();
        mealPlanRecipe.setId(rs.getLong("id"));
        mealPlanRecipe.setMealPlanId(rs.getLong("meal_plan_id"));
        mealPlanRecipe.setRecipeId(rs.getLong("recipe_id"));
        mealPlanRecipe.setDate(rs.getDate("date").toLocalDate());
        mealPlanRecipe.setMealType(rs.getString("meal_type"));
        mealPlanRecipe.setRecipeTitle(rs.getString("recipe_title"));
        return mealPlanRecipe;
    }
}
