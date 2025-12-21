package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.UserMealEntry;
import ru.bmstu.iu6.culinarycompanion.domain.enums.MealType;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserMealEntryDAO {
    
    private final DataSource dataSource;
    
    public UserMealEntryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public UserMealEntry create(UserMealEntry entry) throws SQLException {
        String sql = "INSERT INTO user_meal_entries (user_id, recipe_id, meal_date, meal_type) " +
                     "VALUES (?, ?, ?, ?::meal_type) RETURNING id, created_at";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, entry.getUserId());
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
    
    public List<UserMealEntry> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT ume.*, r.title as recipe_title, r.image_url as recipe_image_url " +
                     "FROM user_meal_entries ume " +
                     "JOIN recipes r ON ume.recipe_id = r.id " +
                     "WHERE ume.user_id = ? AND ume.meal_date >= ? AND ume.meal_date <= ? " +
                     "ORDER BY ume.meal_date, ume.meal_type";
        
        List<UserMealEntry> entries = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }
        
        return entries;
    }
    
    public List<UserMealEntry> findByUserIdAndDate(Long userId, LocalDate date) throws SQLException {
        String sql = "SELECT ume.*, r.title as recipe_title, r.image_url as recipe_image_url " +
                     "FROM user_meal_entries ume " +
                     "JOIN recipes r ON ume.recipe_id = r.id " +
                     "WHERE ume.user_id = ? AND ume.meal_date = ? " +
                     "ORDER BY ume.meal_type";
        
        List<UserMealEntry> entries = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }
        
        return entries;
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM user_meal_entries WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    private UserMealEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        UserMealEntry entry = new UserMealEntry();
        entry.setId(rs.getLong("id"));
        entry.setUserId(rs.getLong("user_id"));
        entry.setRecipeId(rs.getLong("recipe_id"));
        entry.setMealDate(rs.getDate("meal_date").toLocalDate());
        entry.setMealType(MealType.valueOf(rs.getString("meal_type")));
        entry.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        entry.setRecipeTitle(rs.getString("recipe_title"));
        entry.setRecipeImageUrl(rs.getString("recipe_image_url"));
        return entry;
    }
}
