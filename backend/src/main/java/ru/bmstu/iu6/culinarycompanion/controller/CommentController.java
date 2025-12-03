package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.request.CommentCreateRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.CommentResponse;
import ru.bmstu.iu6.culinarycompanion.service.CommentService;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;
import ru.bmstu.iu6.culinarycompanion.exception.ForbiddenException;

import java.util.List;

public class CommentController {
    
    private final CommentService commentService;
    private final Gson gson;
    
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
        this.gson = new Gson();
    }
    
    public void getRecipeComments(Context ctx) {
        try {
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            List<CommentResponse> comments = commentService.getCommentsByRecipeId(recipeId);
            ctx.status(200).json(comments);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void createComment(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            CommentCreateRequest request = gson.fromJson(ctx.body(), CommentCreateRequest.class);
            CommentResponse response = commentService.createComment(userId, recipeId, request);
            ctx.status(201).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ValidationException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void deleteComment(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            Long commentId = Long.parseLong(ctx.pathParam("id"));
            commentService.deleteComment(userId, commentId);
            ctx.status(200).json(new ErrorResponse("Comment deleted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            ctx.status(403).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
