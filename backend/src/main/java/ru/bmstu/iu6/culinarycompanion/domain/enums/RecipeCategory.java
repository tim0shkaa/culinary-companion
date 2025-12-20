package ru.bmstu.iu6.culinarycompanion.domain.enums;

public enum RecipeCategory {
    ЗАВТРАК("Завтрак"),
    ОБЕД("Обед"),
    УЖИН("Ужин"),
    ДЕСЕРТ("Десерт"),
    ПЕРЕКУС("Перекус"),
    ЗАКУСКА("Закуска"),
    СУП("Суп"),
    САЛАТ("Салат");

    private final String displayName;

    RecipeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
