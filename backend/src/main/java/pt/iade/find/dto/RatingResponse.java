package pt.iade.find.dto;

import java.time.LocalDateTime;

public class RatingResponse {

    private Long id;
    private Integer stars;
    private String userEmail;
    private LocalDateTime createdAt;

    public RatingResponse(Long id, Integer stars, String userEmail, LocalDateTime createdAt) {
        this.id = id;
        this.stars = stars;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Integer getStars() {
        return stars;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
