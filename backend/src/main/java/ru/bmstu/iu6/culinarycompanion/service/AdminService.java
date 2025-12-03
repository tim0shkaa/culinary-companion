package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.*;
import ru.bmstu.iu6.culinarycompanion.domain.Report;
import ru.bmstu.iu6.culinarycompanion.domain.User;
import ru.bmstu.iu6.culinarycompanion.domain.enums.ReportStatus;
import ru.bmstu.iu6.culinarycompanion.domain.enums.UserStatus;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserResponse;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminService {
    
    private final UserDAO userDAO;
    private final RecipeDAO recipeDAO;
    private final CommentDAO commentDAO;
    private final ReportDAO reportDAO;
    
    public AdminService(UserDAO userDAO, RecipeDAO recipeDAO, CommentDAO commentDAO, ReportDAO reportDAO) {
        this.userDAO = userDAO;
        this.recipeDAO = recipeDAO;
        this.commentDAO = commentDAO;
        this.reportDAO = reportDAO;
    }
    
    public List<UserResponse> getAllUsers() throws SQLException {
        List<User> users = userDAO.findAll();
        List<UserResponse> responses = new ArrayList<>();
        
        for (User user : users) {
            responses.add(mapToUserResponse(user));
        }
        
        return responses;
    }
    
    public void blockUser(Long userId) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        
        userDAO.updateStatus(userId, UserStatus.BLOCKED);
    }
    
    public void unblockUser(Long userId) throws NotFoundException, SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        
        userDAO.updateStatus(userId, UserStatus.ACTIVE);
    }
    
    public List<Report> getAllReports() throws SQLException {
        return reportDAO.findAll();
    }
    
    public void resolveReport(Long reportId) throws NotFoundException, SQLException {
        Optional<Report> reportOpt = reportDAO.findById(reportId);
        
        if (reportOpt.isEmpty()) {
            throw new NotFoundException("Report not found");
        }
        
        reportDAO.updateStatus(reportId, ReportStatus.RESOLVED);
    }
    
    public void deleteRecipe(Long recipeId) throws NotFoundException, SQLException {
        if (recipeDAO.findById(recipeId).isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }
        
        commentDAO.deleteByRecipeId(recipeId);
        recipeDAO.delete(recipeId);
    }
    
    public void deleteComment(Long commentId) throws NotFoundException, SQLException {
        if (commentDAO.findById(commentId).isEmpty()) {
            throw new NotFoundException("Comment not found");
        }
        
        commentDAO.delete(commentId);
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
