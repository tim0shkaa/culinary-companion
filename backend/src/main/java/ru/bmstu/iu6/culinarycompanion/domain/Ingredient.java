package ru.bmstu.iu6.culinarycompanion.domain;

import ru.bmstu.iu6.culinarycompanion.domain.enums.MeasurementUnit;

public class Ingredient {
    
    private Long id;
    private String name;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Integer calories;
    private MeasurementUnit defaultUnit;
    
    public Ingredient() {
    }
    
    public Ingredient(String name) {
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getProteins() {
        return proteins;
    }
    
    public void setProteins(Double proteins) {
        this.proteins = proteins;
    }
    
    public Double getFats() {
        return fats;
    }
    
    public void setFats(Double fats) {
        this.fats = fats;
    }
    
    public Double getCarbohydrates() {
        return carbohydrates;
    }
    
    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
    
    public Integer getCalories() {
        return calories;
    }
    
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    
    public MeasurementUnit getDefaultUnit() {
        return defaultUnit;
    }
    
    public void setDefaultUnit(MeasurementUnit defaultUnit) {
        this.defaultUnit = defaultUnit;
    }
}
