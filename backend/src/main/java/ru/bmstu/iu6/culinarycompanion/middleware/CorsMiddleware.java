package ru.bmstu.iu6.culinarycompanion.middleware;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class CorsMiddleware implements Handler {
    
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Max-Age", "3600");
        
        if (ctx.method().equals("OPTIONS")) {
            ctx.status(204);
            return;
        }
    }
}
