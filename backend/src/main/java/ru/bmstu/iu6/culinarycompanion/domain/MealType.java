package ru.bmstu.iu6.culinarycompanion.domain.enums;

public enum MealType {
    ЗАВТРАК("Завтрак"),
    ОБЕД("Обед"),
    УЖИН("Ужин"),
    ПЕРЕКУС("Перекус");
    
    private final String displayName;
    
    MealType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
