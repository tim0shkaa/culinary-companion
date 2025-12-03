package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.UserDAO;
import ru.bmstu.iu6.culinarycompanion.domain.User;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserResponse;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    
    private final UserDAO userDAO;
    
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    public UserResponse getUserById(Long userId) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        
        return mapToUserResponse(userOpt.get());
    }
    
    public UserResponse updateUser(Long userId, String body) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        
        User user = userOpt.get();
        userDAO.update(user);
        
        return mapToUserResponse(user);
    }
    
    public void deleteUser(Long userId) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        
        userDAO.delete(userId);
    }
    
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setCreatedAt(user.getCreatedAt().toString());
        return response;
    }
}
