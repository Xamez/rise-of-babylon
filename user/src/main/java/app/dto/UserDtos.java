package app.dto;

import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDtos {

    public record UserLogin(
            @NotNull
            @NotBlank
            String username,

            @NotNull
            @NotBlank
            String password) {
    }

    public record UserSignup(
            @NotNull
            @NotBlank
            @Size(min = 4, max = 35)
            @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username must contain only alphanumeric characters, underscores and hyphens")
            String username,

            @NotNull
            @Email
            String email,
            @NotNull
            @NotBlank String password) {
    }

    public record TokenResponse(String token) {
    }
}
