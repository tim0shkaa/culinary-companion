package ru.bmstu.iu6.culinarycompanion.service;

import ru.bmstu.iu6.culinarycompanion.dao.CommentDAO;
import ru.bmstu.iu6.culinarycompanion.dao.RecipeDAO;
import ru.bmstu.iu6.culinarycompanion.domain.Comment;
import ru.bmstu.iu6.culinarycompanion.dto.request.CommentCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.CommentResponse;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.util.ValidationUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentService {
    
    private final CommentDAO commentDAO;
    private final RecipeDAO recipeDAO;
    
    public CommentService(CommentDAO commentDAO, RecipeDAO recipeDAO) {
        this.commentDAO = commentDAO;
        this.recipeDAO = recipeDAO;
    }
    
    public List<CommentResponse> getCommentsByRecipeId(Long recipeId) throws SQLException {
        List<Comment> comments = commentDAO.findByRecipeId(recipeId);
        List<CommentResponse> responses = new ArrayList<>();
        
        for (Comment comment : comments) {
            responses.add(mapToCommentResponse(comment));
        }
        
        return responses;
    }
    
    public CommentResponse createComment(Long userId, Long recipeId, CommentCreateRequest request) 
            throws NotFoundException, ValidationException, SQLException {
        
        ValidationUtil.validateNotEmpty(request.getText(), "Comment text");
        
        if (recipeDAO.findById(recipeId).isEmpty()) {
            throw new NotFoundException("Recipe not found");
        }
        
        Comment comment = new Comment();
        comment.setRecipeId(recipeId);
        comment.setUserId(userId);
        comment.setText(request.getText());
        
        comment = commentDAO.create(comment);
        
        Optional<Comment> commentOpt = commentDAO.findById(comment.getId());
        
        return mapToCommentResponse(commentOpt.orElse(comment));
    }
    
    public void deleteComment(Long userId, Long commentId) 
            throws NotFoundException, ForbiddenException, SQLException {
        
        Optional<Comment> commentOpt = commentDAO.findById(commentId);
        
        if (commentOpt.isEmpty()) {
            throw new NotFoundException("Comment not found");
        }
        
        Comment comment = commentOpt.get();
        
        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own comments");
        }
        
        commentDAO.delete(commentId);
    }
    
    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setRecipeId(comment.getRecipeId());
        response.setUserId(comment.getUserId());
        response.setUsername(comment.getUsername());
        response.setText(comment.getText());
        response.setCreatedAt(comment.getCreatedAt().toString());
        return response;
    }
}
