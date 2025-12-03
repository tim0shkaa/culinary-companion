package ru.bmstu.iu6.culinarycompanion.dao;

import ru.bmstu.iu6.culinarycompanion.domain.Report;
import ru.bmstu.iu6.culinarycompanion.domain.enums.ReportStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportDAO {
    
    private final DataSource dataSource;
    
    public ReportDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Report create(Report report) throws SQLException {
        String sql = "INSERT INTO reports (reporter_id, content_type, content_id, reason, status, created_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, report.getReporterId());
            stmt.setString(2, report.getContentType());
            stmt.setLong(3, report.getContentId());
            stmt.setString(4, report.getReason());
            stmt.setString(5, report.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(report.getCreatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                report.setId(rs.getLong("id"));
            }
            
            return report;
        }
    }
    
    public Optional<Report> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM reports WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToReport(rs));
            }
            
            return Optional.empty();
        }
    }
    
    public List<Report> findAll() throws SQLException {
        String sql = "SELECT * FROM reports ORDER BY created_at DESC";
        List<Report> reports = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        }
        
        return reports;
    }
    
    public List<Report> findByStatus(ReportStatus status) throws SQLException {
        String sql = "SELECT * FROM reports WHERE status = ? ORDER BY created_at DESC";
        List<Report> reports = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        }
        
        return reports;
    }
    
    public void updateStatus(Long id, ReportStatus status) throws SQLException {
        String sql = "UPDATE reports SET status = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM reports WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getLong("id"));
        report.setReporterId(rs.getLong("reporter_id"));
        report.setContentType(rs.getString("content_type"));
        report.setContentId(rs.getLong("content_id"));
        report.setReason(rs.getString("reason"));
        report.setStatus(ReportStatus.valueOf(rs.getString("status")));
        report.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return report;
    }
}
