package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.UserDAO;
import ru.bmstu.iu6.culinarycompanion.domain.User;
import ru.bmstu.iu6.culinarycompanion.domain.enums.UserRole;
import ru.bmstu.iu6.culinarycompanion.domain.enums.UserStatus;
import ru.bmstu.iu6.culinarycompanion.dto.request.LoginRequest;
import ru.bmstu.iu6.culinarycompanion.dto.request.RegisterRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.AuthResponse;
import ru.bmstu.iu6.culinarycompanion.exception.AuthenticationException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.util.JwtUtil;
import ru.bmstu.iu6.culinarycompanion.util.PasswordUtil;
import ru.bmstu.iu6.culinarycompanion.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    
    private final UserDAO userDAO;
    private final JwtUtil jwtUtil;
    
    public AuthService(UserDAO userDAO, JwtUtil jwtUtil) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
    }
    
    public AuthResponse register(RegisterRequest request) throws ValidationException, SQLException {
        ValidationUtil.validateEmail(request.getEmail());
        ValidationUtil.validatePassword(request.getPassword());
        ValidationUtil.validateUsername(request.getUsername());
        
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        if (userDAO.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(PasswordUtil.hashPassword(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        
        user = userDAO.create(user);
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
    
    public AuthResponse login(LoginRequest request) throws AuthenticationException, ValidationException, SQLException {
        ValidationUtil.validateEmail(request.getEmail());
        ValidationUtil.validateNotEmpty(request.getPassword(), "Password");
        
        Optional<User> userOpt = userDAO.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AuthenticationException("Account is blocked");
        }
        
        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
    
    public void logout(String token) {
    }
}
