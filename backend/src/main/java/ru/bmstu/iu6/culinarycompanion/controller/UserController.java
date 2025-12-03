package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserResponse;
import ru.bmstu.iu6.culinarycompanion.service.UserService;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;

public class UserController {
    
    private final UserService userService;
    private final Gson gson;
    
    public UserController(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }
    
    public void getCurrentUser(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            UserResponse response = userService.getUserById(userId);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void getUserById(Context ctx) {
        try {
            Long userId = Long.parseLong(ctx.pathParam("id"));
            UserResponse response = userService.getUserById(userId);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("Invalid user ID"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void updateProfile(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            String body = ctx.body();
            UserResponse response = userService.updateUser(userId, body);
            ctx.status(200).json(response);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void deleteProfile(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            userService.deleteUser(userId);
            ctx.status(200).json(new ErrorResponse("User deleted successfully"));
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
