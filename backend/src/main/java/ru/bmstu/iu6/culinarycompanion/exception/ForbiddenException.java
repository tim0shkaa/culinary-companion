package ru.bmstu.iu6.culinarycompanion.exception;

public class ForbiddenException extends Exception {
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
