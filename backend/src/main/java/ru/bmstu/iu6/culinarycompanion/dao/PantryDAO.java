package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.PantryItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PantryDAO {
    
    private final DataSource dataSource;
    
    public PantryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public PantryItem create(PantryItem pantryItem) throws SQLException {
        String sql = "INSERT INTO pantry (user_id, ingredient_id) VALUES (?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, pantryItem.getUserId());
            stmt.setLong(2, pantryItem.getIngredientId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pantryItem.setId(rs.getLong("id"));
            }
            
            return pantryItem;
        }
    }
    
    public List<PantryItem> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT p.*, i.name as ingredient_name FROM pantry p " +
                     "JOIN ingredients i ON p.ingredient_id = i.id WHERE p.user_id = ? ORDER BY i.name";
        List<PantryItem> pantryItems = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pantryItems.add(mapResultSetToPantryItem(rs));
            }
        }
        
        return pantryItems;
    }
    
    public Optional<PantryItem> findByUserIdAndIngredientId(Long userId, Long ingredientId) throws SQLException {
        String sql = "SELECT p.*, i.name as ingredient_name FROM pantry p " +
                     "JOIN ingredients i ON p.ingredient_id = i.id WHERE p.user_id = ? AND p.ingredient_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, ingredientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPantryItem(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM pantry WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public void deleteByUserIdAndIngredientId(Long userId, Long ingredientId) throws SQLException {
        String sql = "DELETE FROM pantry WHERE user_id = ? AND ingredient_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, ingredientId);
            stmt.executeUpdate();
        }
    }
    
    public boolean existsByUserIdAndIngredientId(Long userId, Long ingredientId) throws SQLException {
        String sql = "SELECT EXISTS(SELECT 1 FROM pantry WHERE user_id = ? AND ingredient_id = ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, ingredientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean(1);
            }
            
            return false;
        }
    }
    
    private PantryItem mapResultSetToPantryItem(ResultSet rs) throws SQLException {
        PantryItem pantryItem = new PantryItem();
        pantryItem.setId(rs.getLong("id"));
        pantryItem.setUserId(rs.getLong("user_id"));
        pantryItem.setIngredientId(rs.getLong("ingredient_id"));
        pantryItem.setIngredientName(rs.getString("ingredient_name"));
        return pantryItem;
    }
}
