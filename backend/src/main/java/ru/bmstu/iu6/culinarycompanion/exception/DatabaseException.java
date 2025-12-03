package ru.bmstu.iu6.culinarycompanion.exception;

public class DatabaseException extends Exception {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
