package app.messaging;

import io.smallrye.common.constraint.NotNull;
public record PasswordResetMessage(
        @NotNull String email,
        @NotNull String username,
        @NotNull String token
) {
}

