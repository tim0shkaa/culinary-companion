package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.MealPlanEntry;
import ru.bmstu.iu6.culinarycompanion.domain.enums.MealType;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MealPlanEntryDAO {
    
    private final DataSource dataSource;
    
    public MealPlanEntryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public MealPlanEntry create(MealPlanEntry entry) throws SQLException {
        String sql = "INSERT INTO meal_plan_entries (meal_plan_id, recipe_id, meal_date, meal_type) " +
                     "VALUES (?, ?, ?, ?::meal_type) RETURNING id, created_at";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, entry.getMealPlanId());
            stmt.setLong(2, entry.getRecipeId());
            stmt.setDate(3, Date.valueOf(entry.getMealDate()));
            stmt.setString(4, entry.getMealType().name());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                entry.setId(rs.getLong("id"));
                entry.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            
            return entry;
        }
    }
    
    public List<MealPlanEntry> findByMealPlanId(Long mealPlanId) throws SQLException {
        String sql = "SELECT mpe.*, r.title as recipe_title, r.image_url as recipe_image_url " +
                     "FROM meal_plan_entries mpe " +
                     "JOIN recipes r ON mpe.recipe_id = r.id " +
                     "WHERE mpe.meal_plan_id = ? " +
                     "ORDER BY mpe.meal_date, mpe.meal_type";
        
        List<MealPlanEntry> entries = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlanId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }
        
        return entries;
    }
    
    public List<MealPlanEntry> findByMealPlanIdAndDate(Long mealPlanId, LocalDate date) throws SQLException {
        String sql = "SELECT mpe.*, r.title as recipe_title, r.image_url as recipe_image_url " +
                     "FROM meal_plan_entries mpe " +
                     "JOIN recipes r ON mpe.recipe_id = r.id " +
                     "WHERE mpe.meal_plan_id = ? AND mpe.meal_date = ? " +
                     "ORDER BY mpe.meal_type";
        
        List<MealPlanEntry> entries = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlanId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }
        
        return entries;
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM meal_plan_entries WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public void deleteByMealPlanId(Long mealPlanId) throws SQLException {
        String sql = "DELETE FROM meal_plan_entries WHERE meal_plan_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlanId);
            stmt.executeUpdate();
        }
    }
    
    private MealPlanEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        MealPlanEntry entry = new MealPlanEntry();
        entry.setId(rs.getLong("id"));
        entry.setMealPlanId(rs.getLong("meal_plan_id"));
        entry.setRecipeId(rs.getLong("recipe_id"));
        entry.setMealDate(rs.getDate("meal_date").toLocalDate());
        entry.setMealType(MealType.valueOf(rs.getString("meal_type")));
        entry.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        entry.setRecipeTitle(rs.getString("recipe_title"));
        entry.setRecipeImageUrl(rs.getString("recipe_image_url"));
        return entry;
    }
}
