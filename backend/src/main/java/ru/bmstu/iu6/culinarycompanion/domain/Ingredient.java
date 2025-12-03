package ru.bmstu.iu6.culinarycompanion.domain;

public class Ingredient {
    
    private Long id;
    private String name;
    
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
}
