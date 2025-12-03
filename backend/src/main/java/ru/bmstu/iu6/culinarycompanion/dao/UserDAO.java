package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.User;
import ru.bmstu.iu6.culinarycompanion.domain.enums.UserRole;
import ru.bmstu.iu6.culinarycompanion.domain.enums.UserStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    
    private final DataSource dataSource;
    
    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, username, role, status, created_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getLong("id"));
            }
            
            return user;
        }
    }
    
    public Optional<User> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, username = ?, role = ?, status = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getStatus().name());
            stmt.setLong(5, user.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void updatePassword(Long userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, passwordHash);
            stmt.setLong(2, userId);
            
            stmt.executeUpdate();
        }
    }
    
    public void updateStatus(Long userId, UserStatus status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setLong(2, userId);
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean(1);
            }
            
            return false;
        }
    }
    
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean(1);
            }
            
            return false;
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setUsername(rs.getString("username"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
