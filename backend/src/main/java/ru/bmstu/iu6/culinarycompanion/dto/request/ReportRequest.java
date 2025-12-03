package ru.bmstu.iu6.culinarycompanion.dto.request;

public class ReportRequest {
    
    private String contentType;
    private Long contentId;
    private String reason;
    
    public ReportRequest() {
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getContentId() {
        return contentId;
    }
    
    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
