package ru.bmstu.iu6.culinarycompanion.util;

import ru.bmstu.iu6.culinarycompanion.exception.ValidationException;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }
    }
    
    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password is required");
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new ValidationException("Password must not exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
    }
    
    public static void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        
        if (username.length() < MIN_USERNAME_LENGTH) {
            throw new ValidationException("Username must be at least " + MIN_USERNAME_LENGTH + " characters");
        }
        
        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new ValidationException("Username must not exceed " + MAX_USERNAME_LENGTH + " characters");
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required");
        }
    }
    
    public static void validatePositive(Integer value, String fieldName) throws ValidationException {
        if (value == null || value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }
    
    public static void validateRating(Integer rating) throws ValidationException {
        if (rating == null) {
            throw new ValidationException("Rating is required");
        }
        
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
    }
}
