package pt.iade.find.dto;

import pt.iade.find.model.Bench;
import pt.iade.find.model.BenchStatus;
import pt.iade.find.model.BenchType;

import java.time.LocalDateTime;

public class BenchResponse {

    private Long id;
    private Double latitude;
    private Double longitude;
    private BenchType type;
    private String color;
    private Double widthMeters;
    private BenchStatus status;
    private String reportedByEmail;
    private LocalDateTime createdAt;
    private Double averageRating;
    private Long ratingCount;

    public static BenchResponse from(Bench bench, Double averageRating, Long ratingCount) {
        BenchResponse response = new BenchResponse();
        response.id = bench.getId();
        response.latitude = bench.getLatitude();
        response.longitude = bench.getLongitude();
        response.type = bench.getType();
        response.color = bench.getColor();
        response.widthMeters = bench.getWidthMeters();
        response.status = bench.getStatus();
        response.reportedByEmail = bench.getReportedBy() != null ? bench.getReportedBy().getEmail() : null;
        response.createdAt = bench.getCreatedAt();
        response.averageRating = averageRating;
        response.ratingCount = ratingCount;
        return response;
    }

    public Long getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public BenchType getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public Double getWidthMeters() {
        return widthMeters;
    }

    public BenchStatus getStatus() {
        return status;
    }

    public String getReportedByEmail() {
        return reportedByEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Long getRatingCount() {
        return ratingCount;
    }
}
