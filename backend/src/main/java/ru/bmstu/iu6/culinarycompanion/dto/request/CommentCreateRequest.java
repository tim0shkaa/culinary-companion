package ru.bmstu.iu6.culinarycompanion.dto.request;

public class CommentCreateRequest {
    
    private String text;
    
    public CommentCreateRequest() {
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
