package ru.bmstu.iu6.culinarycompanion.middleware;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ru.bmstu.iu6.culinarycompanion.util.JwtUtil;
import ru.bmstu.iu6.culinarycompanion.dto.response.ErrorResponse;
import ru.bmstu.iu6.culinarycompanion.dao.UserDAO;
import ru.bmstu.iu6.culinarycompanion.domain.User;
import ru.bmstu.iu6.culinarycompanion.domain.enums.UserRole;

import java.util.Optional;

public class AdminMiddleware implements Handler {
    
    private final JwtUtil jwtUtil;
    private final UserDAO userDAO;
    
    public AdminMiddleware(JwtUtil jwtUtil, UserDAO userDAO) {
        this.jwtUtil = jwtUtil;
        this.userDAO = userDAO;
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
            Optional<User> userOpt = userDAO.findById(userId);
            
            if (userOpt.isEmpty()) {
                ctx.status(401).json(new ErrorResponse("User not found"));
                return;
            }
            
            User user = userOpt.get();
            
            if (user.getRole() != UserRole.ADMIN) {
                ctx.status(403).json(new ErrorResponse("Admin access required"));
                return;
            }
            
            ctx.attribute("userId", userId);
        } catch (Exception e) {
            ctx.status(401).json(new ErrorResponse("Invalid or expired token"));
        }
    }
}
