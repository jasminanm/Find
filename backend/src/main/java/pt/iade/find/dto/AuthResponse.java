package pt.iade.find.dto;

import pt.iade.find.model.Role;

public class AuthResponse {

    private String token;
    private Long userId;
    private String email;
    private Role role;

    public AuthResponse(String token, Long userId, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
