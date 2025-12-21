package ru.bmstu.iu6.culinarycompanion.dto.response;

public class DayNutritionResponse {

    private String date;
    private Double totalProteins;
    private Double totalFats;
    private Double totalCarbohydrates;  // ← ПЕРЕИМЕНУЙ
    private Integer totalCalories;

    public DayNutritionResponse() {
    }

    public DayNutritionResponse(String date, Double totalProteins, Double totalFats,
                                Double totalCarbohydrates, Integer totalCalories) {  // ← И ТУТ
        this.date = date;
        this.totalProteins = totalProteins;
        this.totalFats = totalFats;
        this.totalCarbohydrates = totalCarbohydrates;  // ← И ТУТ
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

    public Double getTotalCarbohydrates() {  // ← ПЕРЕИМЕНУЙ ГЕТТЕР
        return totalCarbohydrates;
    }

    public void setTotalCarbohydrates(Double totalCarbohydrates) {  // ← И СЕТТЕР
        this.totalCarbohydrates = totalCarbohydrates;
    }

    public Integer getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }
}