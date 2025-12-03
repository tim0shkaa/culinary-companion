package ru.bmstu.iu6.culinarycompanion.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    
    private static HikariDataSource dataSource;
    
    public static DataSource getDataSource() {
        if (dataSource == null) {
            initialize();
        }
        return dataSource;
    }
    
    private static void initialize() {
        try {
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            
            if (input != null) {
                props.load(input);
            }
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url", "jdbc:postgresql://localhost:5432/culinary_companion"));
            config.setUsername(props.getProperty("db.username", "postgres"));
            config.setPassword(props.getProperty("db.password", "postgres"));
            config.setDriverClassName("org.postgresql.Driver");
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }
    
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
