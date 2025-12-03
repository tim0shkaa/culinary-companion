package ru.bmstu.iu6.culinarycompanion.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import ru.bmstu.iu6.culinarycompanion.dto.request.LoginRequest;
import ru.bmstu.iu6.culinarycompanion.dto.request.RegisterRequest;
import ru.bmstu.iu6.culinarycompanion.dto.response.AuthResponse;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.service.AuthService;
import ru.bmstu.iu6.culinarycompanion.exception.AuthenticationException;
import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;

public class AuthController {
    
    private final AuthService authService;
    private final Gson gson;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
        this.gson = new Gson();
    }
    
    public void register(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            AuthResponse response = authService.register(request);
            ctx.status(201).json(response);
        } catch (ValidationException e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void login(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            AuthResponse response = authService.login(request);
            ctx.status(200).json(response);
        } catch (AuthenticationException e) {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
    
    public void logout(Context ctx) {
        try {
            String token = ctx.header("Authorization");
            authService.logout(token);
            ctx.status(200).json(new ErrorResponse("Logged out successfully"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        }
    }
}
