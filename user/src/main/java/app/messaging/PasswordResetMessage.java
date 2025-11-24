package app.messaging;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.common.constraint.NotNull;

@RegisterForReflection
public record PasswordResetMessage(
        @NotNull String email,
        @NotNull String username,
        @NotNull String token
) {
}

