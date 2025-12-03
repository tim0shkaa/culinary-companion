package ru.bmstu.iu6.culinarycompanion.middleware;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ru.bmstu.iu6.culinarycompanion.util.JwtUtil;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;

public class AuthMiddleware implements Handler {
    
    private final JwtUtil jwtUtil;
    
    public AuthMiddleware(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public void handle(Context ctx) throws Exception {
        String authHeader = ctx.header("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).json(new ErrorResponse("Missing or invalid authorization header"));
            return;
        }
        
        String token = authHeader.substring(7);
        
        try {
            Long userId = jwtUtil.validateTokenAndGetUserId(token);
            ctx.attribute("userId", userId);
        } catch (Exception e) {
            ctx.status(401).json(new ErrorResponse("Invalid or expired token"));
        }
    }
}
