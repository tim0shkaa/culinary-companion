package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.UserStatsResponse;
import ru.bmstu.iu6.culinarycompanion.exception.NotFoundException;
import ru.bmstu.iu6.culinarycompanion.service.UserService;

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
            UserResponse user = userService.getUserById(userId);
            ctx.status(200).json(user);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void getUserById(Context ctx) {
        try {
            Long userId = Long.parseLong(ctx.pathParam("id"));
            UserResponse user = userService.getUserById(userId);
            ctx.status(200).json(user);
        } catch (NotFoundException e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void getUserStats(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            UserStatsResponse stats = userService.getUserStats(userId);
            ctx.status(200).json(stats);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }

    public void updateProfile(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            UserResponse user = userService.updateUser(userId, ctx.body());
            ctx.status(200).json(user);
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
