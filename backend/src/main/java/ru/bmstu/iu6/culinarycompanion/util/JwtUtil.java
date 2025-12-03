package ru.bmstu.iu6.culinarycompanion.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {
    
    private static final String SECRET_KEY = "your-secret-key-change-in-production";
    private static final long EXPIRATION_TIME = 86400000;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    
    public JwtUtil() {
        this.algorithm = Algorithm.HMAC256(SECRET_KEY);
        this.verifier = JWT.require(algorithm).build();
    }
    
    public String generateToken(Long userId, String email, String role) {
        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }
    
    public Long validateTokenAndGetUserId(String token) throws JWTVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        return Long.parseLong(jwt.getSubject());
    }
    
    public String getEmailFromToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("email").asString();
    }
    
    public String getRoleFromToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("role").asString();
    }
}
