package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserResponse;
import ru.bmstu.iu6.culinarycompanion.service.AdminService;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.domain.Report;

import java.util.List;

public class AdminController {
    
    private final AdminService adminService;
    private final Gson gson;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
        this.gson = new Gson();
    }
    
    public void getAllUsers(Context ctx) {
        try {
            List<UserResponse> users = adminService.getAllUsers();
            ctx.status(200).json(users);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void blockUser(Context ctx) {
        try {
            Long userId = Long.parseLong(ctx.pathParam("id"));
            adminService.blockUser(userId);
            ctx.status(200).json(new ErrorResponse("User blocked successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void unblockUser(Context ctx) {
        try {
            Long userId = Long.parseLong(ctx.pathParam("id"));
            adminService.unblockUser(userId);
            ctx.status(200).json(new ErrorResponse("User unblocked successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getAllReports(Context ctx) {
        try {
            List<Report> reports = adminService.getAllReports();
            ctx.status(200).json(reports);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void resolveReport(Context ctx) {
        try {
            Long reportId = Long.parseLong(ctx.pathParam("id"));
            adminService.resolveReport(reportId);
            ctx.status(200).json(new ErrorResponse("Report resolved successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void deleteRecipe(Context ctx) {
        try {
            Long recipeId = Long.parseLong(ctx.pathParam("recipeId"));
            adminService.deleteRecipe(recipeId);
            ctx.status(200).json(new ErrorResponse("Recipe deleted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void deleteComment(Context ctx) {
        try {
            Long commentId = Long.parseLong(ctx.pathParam("commentId"));
            adminService.deleteComment(commentId);
            ctx.status(200).json(new ErrorResponse("Comment deleted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
