package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.ShoppingList;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingListDAO {
    
    private final DataSource dataSource;
    
    public ShoppingListDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public ShoppingList create(ShoppingList shoppingList) throws SQLException {
        String sql = "INSERT INTO shopping_lists (user_id, meal_plan_id, created_at) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, shoppingList.getUserId());
            stmt.setLong(2, shoppingList.getMealPlanId());
            stmt.setTimestamp(3, Timestamp.valueOf(shoppingList.getCreatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                shoppingList.setId(rs.getLong("id"));
            }
            
            return shoppingList;
        }
    }
    
    public Optional<ShoppingList> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM shopping_lists WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToShoppingList(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public Optional<ShoppingList> findByMealPlanId(Long mealPlanId) throws SQLException {
        String sql = "SELECT * FROM shopping_lists WHERE meal_plan_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, mealPlanId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToShoppingList(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<ShoppingList> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM shopping_lists WHERE user_id = ? ORDER BY created_at DESC";
        List<ShoppingList> shoppingLists = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                shoppingLists.add(mapResultSetToShoppingList(rs));
            }
        }
        
        return shoppingLists;
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM shopping_lists WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    private ShoppingList mapResultSetToShoppingList(ResultSet rs) throws SQLException {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(rs.getLong("id"));
        shoppingList.setUserId(rs.getLong("user_id"));
        shoppingList.setMealPlanId(rs.getLong("meal_plan_id"));
        shoppingList.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return shoppingList;
    }
}
