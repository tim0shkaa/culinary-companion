package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.Comment;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentDAO {
    
    private final DataSource dataSource;
    
    public CommentDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Comment create(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (recipe_id, user_id, text, created_at) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, comment.getRecipeId());
            stmt.setLong(2, comment.getUserId());
            stmt.setString(3, comment.getText());
            stmt.setTimestamp(4, Timestamp.valueOf(comment.getCreatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                comment.setId(rs.getLong("id"));
            }
            
            return comment;
        }
    }
    
    public Optional<Comment> findById(Long id) throws SQLException {
        String sql = "SELECT c.*, u.username FROM comments c " +
                     "JOIN users u ON c.user_id = u.id WHERE c.id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToComment(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<Comment> findByRecipeId(Long recipeId) throws SQLException {
        String sql = "SELECT c.*, u.username FROM comments c " +
                     "JOIN users u ON c.user_id = u.id WHERE c.recipe_id = ? ORDER BY c.created_at DESC";
        List<Comment> comments = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        
        return comments;
    }
    
    public List<Comment> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT c.*, u.username FROM comments c " +
                     "JOIN users u ON c.user_id = u.id WHERE c.user_id = ? ORDER BY c.created_at DESC";
        List<Comment> comments = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        
        return comments;
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public void deleteByRecipeId(Long recipeId) throws SQLException {
        String sql = "DELETE FROM comments WHERE recipe_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, recipeId);
            stmt.executeUpdate();
        }
    }
    
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setRecipeId(rs.getLong("recipe_id"));
        comment.setUserId(rs.getLong("user_id"));
        comment.setText(rs.getString("text"));
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        comment.setUsername(rs.getString("username"));
        return comment;
    }
}
