package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.Rating;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class RatingDAO {
    
    private final DataSource dataSource;
    
    public RatingDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Rating create(Rating rating) throws SQLException {
        String sql = "INSERT INTO ratings (recipe_id, user_id, rating, created_at) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rating.getRecipeId());
            stmt.setLong(2, rating.getUserId());
            stmt.setInt(3, rating.getRating());
            stmt.setTimestamp(4, Timestamp.valueOf(rating.getCreatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rating.setId(rs.getLong("id"));
            }
            
            return rating;
        }
    }
    
    public Optional<Rating> findByRecipeIdAndUserId(Long recipeId, Long userId) throws SQLException {
        String sql = "SELECT * FROM ratings WHERE recipe_id = ? AND user_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRating(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public void update(Rating rating) throws SQLException {
        String sql = "UPDATE ratings SET rating = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rating.getRating());
            stmt.setLong(2, rating.getId());
            
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
    
    public int getRatingCount(Long recipeId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ratings WHERE recipe_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
            return 0;
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM ratings WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public void deleteByRecipeId(Long recipeId) throws SQLException {
        String sql = "DELETE FROM ratings WHERE recipe_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            stmt.executeUpdate();
        }
    }
    
    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getLong("id"));
        rating.setRecipeId(rs.getLong("recipe_id"));
        rating.setUserId(rs.getLong("user_id"));
        rating.setRating(rs.getInt("rating"));
        rating.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return rating;
    }
}
