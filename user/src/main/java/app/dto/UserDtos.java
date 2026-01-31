package app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDtos {

    public static final String PASSWORD_POLICY_REGEX = "^(?=.*[A-Z])(?=.*\\d).{6,}$";
    public static final String PASSWORD_POLICY_MESSAGE = "Password must be at least 6 characters long and contain at least one uppercase letter and one number";

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
            @NotBlank
            @Email
            String email,

            @NotNull
            @NotBlank
            @Pattern(regexp = PASSWORD_POLICY_REGEX, message = PASSWORD_POLICY_MESSAGE)
            String password) {
    }

    public record TokenResponse(
             @NotNull
             @NotBlank
             String accessToken,

             @NotNull
             @NotBlank
             String refreshToken,

             @NotNull
             @NotBlank
             Long refreshTokenExpiresIn
    ) {
    }

    public record PasswordResetConfirm(
            @NotNull
            @NotBlank
            String passwordResetToken,

            @NotNull
            @NotBlank
            @Pattern(regexp = PASSWORD_POLICY_REGEX, message = PASSWORD_POLICY_MESSAGE)
            String newPassword) {
    }

}
