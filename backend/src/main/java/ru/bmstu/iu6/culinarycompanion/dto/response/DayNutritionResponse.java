package ru.bmstu.iu6.culinarycompanion.dto.response;

public class DayNutritionResponse {
    
    private String date;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbs;
    private Integer totalCalories;
    
    public DayNutritionResponse() {
    }
    
    public DayNutritionResponse(String date, Double totalProteins, Double totalFats, 
                               Double totalCarbs, Integer totalCalories) {
        this.date = date;
        this.totalProteins = totalProteins;
        this.totalFats = totalFats;
        this.totalCarbs = totalCarbs;
        this.totalCalories = totalCalories;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public Double getTotalProteins() {
        return totalProteins;
    }
    
    public void setTotalProteins(Double totalProteins) {
        this.totalProteins = totalProteins;
    }
    
    public Double getTotalFats() {
        return totalFats;
    }
    
    public void setTotalFats(Double totalFats) {
        this.totalFats = totalFats;
    }
    
    public Double getTotalCarbs() {
        return totalCarbs;
    }
    
    public void setTotalCarbs(Double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }
    
    public Integer getTotalCalories() {
        return totalCalories;
    }
    
    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }
}
