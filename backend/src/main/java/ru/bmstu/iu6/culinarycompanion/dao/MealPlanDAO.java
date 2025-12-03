package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.MealPlan;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MealPlanDAO {
    
    private final DataSource dataSource;
    
    public MealPlanDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public MealPlan create(MealPlan mealPlan) throws SQLException {
        String sql = "INSERT INTO meal_plans (user_id, name, start_date, end_date, created_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlan.getUserId());
            stmt.setString(2, mealPlan.getName());
            stmt.setDate(3, Date.valueOf(mealPlan.getStartDate()));
            stmt.setDate(4, Date.valueOf(mealPlan.getEndDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(mealPlan.getCreatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                mealPlan.setId(rs.getLong("id"));
            }
            
            return mealPlan;
        }
    }
    
    public Optional<MealPlan> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM meal_plans WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToMealPlan(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<MealPlan> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM meal_plans WHERE user_id = ? ORDER BY start_date DESC";
        List<MealPlan> mealPlans = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mealPlans.add(mapResultSetToMealPlan(rs));
            }
        }
        
        return mealPlans;
    }
    
    public void update(MealPlan mealPlan) throws SQLException {
        String sql = "UPDATE meal_plans SET name = ?, start_date = ?, end_date = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, mealPlan.getName());
            stmt.setDate(2, Date.valueOf(mealPlan.getStartDate()));
            stmt.setDate(3, Date.valueOf(mealPlan.getEndDate()));
            stmt.setLong(4, mealPlan.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM meal_plans WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    private MealPlan mapResultSetToMealPlan(ResultSet rs) throws SQLException {
        MealPlan mealPlan = new MealPlan();
        mealPlan.setId(rs.getLong("id"));
        mealPlan.setUserId(rs.getLong("user_id"));
        mealPlan.setName(rs.getString("name"));
        mealPlan.setStartDate(rs.getDate("start_date").toLocalDate());
        mealPlan.setEndDate(rs.getDate("end_date").toLocalDate());
        mealPlan.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return mealPlan;
    }
}
