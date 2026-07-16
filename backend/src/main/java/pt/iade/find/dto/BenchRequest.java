package pt.iade.find.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.iade.find.model.BenchType;

public class BenchRequest {

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @NotNull
    private BenchType type;

    @NotBlank
    private String color;

    @NotNull
    @DecimalMin("0.1")
    private Double widthMeters;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public BenchType getType() {
        return type;
    }

    public void setType(BenchType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getWidthMeters() {
        return widthMeters;
    }

    public void setWidthMeters(Double widthMeters) {
        this.widthMeters = widthMeters;
    }
}
