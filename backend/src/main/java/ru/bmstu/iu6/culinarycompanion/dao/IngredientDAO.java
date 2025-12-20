package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.Ingredient;
import ru.bmstu.iu6.culinarycompanion.domain.enums.MeasurementUnit;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientDAO {
    
    private final DataSource dataSource;
    
    public IngredientDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Ingredient create(Ingredient ingredient) throws SQLException {
        String sql = "INSERT INTO ingredients (name, proteins, fats, carbohydrates, calories, default_unit) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ingredient.getName());
            stmt.setDouble(2, ingredient.getProteins() != null ? ingredient.getProteins() : 0);
            stmt.setDouble(3, ingredient.getFats() != null ? ingredient.getFats() : 0);
            stmt.setDouble(4, ingredient.getCarbohydrates() != null ? ingredient.getCarbohydrates() : 0);
            stmt.setInt(5, ingredient.getCalories() != null ? ingredient.getCalories() : 0);
            stmt.setString(6, ingredient.getDefaultUnit() != null ? ingredient.getDefaultUnit().name() : MeasurementUnit.ГРАММ.name());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ingredient.setId(rs.getLong("id"));
            }
            
            return ingredient;
        }
    }
    
    public Optional<Ingredient> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM ingredients WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToIngredient(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public Optional<Ingredient> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM ingredients WHERE LOWER(name) = LOWER(?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToIngredient(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<Ingredient> findAll() throws SQLException {
        String sql = "SELECT * FROM ingredients ORDER BY name";
        List<Ingredient> ingredients = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ingredients.add(mapResultSetToIngredient(rs));
            }
        }
        
        return ingredients;
    }
    
    public List<Ingredient> search(String query) throws SQLException {
        String sql = "SELECT * FROM ingredients WHERE LOWER(name) LIKE ? ORDER BY name LIMIT 20";
        List<Ingredient> ingredients = new ArrayList<>();
        String searchPattern = "%" + query.toLowerCase() + "%";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ingredients.add(mapResultSetToIngredient(rs));
            }
        }
        
        return ingredients;
    }
    
    public Ingredient findOrCreate(String name) throws SQLException {
        Optional<Ingredient> existing = findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setProteins(0.0);
        ingredient.setFats(0.0);
        ingredient.setCarbohydrates(0.0);
        ingredient.setCalories(0);
        ingredient.setDefaultUnit(MeasurementUnit.ГРАММ);
        return create(ingredient);
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM ingredients WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Ingredient mapResultSetToIngredient(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getLong("id"));
        ingredient.setName(rs.getString("name"));
        ingredient.setProteins(rs.getDouble("proteins"));
        ingredient.setFats(rs.getDouble("fats"));
        ingredient.setCarbohydrates(rs.getDouble("carbohydrates"));
        ingredient.setCalories(rs.getInt("calories"));
        
        String unitStr = rs.getString("default_unit");
        if (unitStr != null) {
            try {
                ingredient.setDefaultUnit(MeasurementUnit.valueOf(unitStr));
            } catch (IllegalArgumentException e) {
                ingredient.setDefaultUnit(MeasurementUnit.ГРАММ);
            }
        } else {
            ingredient.setDefaultUnit(MeasurementUnit.ГРАММ);
        }
        
        return ingredient;
    }
}
