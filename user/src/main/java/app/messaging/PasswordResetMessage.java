package app.messaging;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;

@RegisterForReflection
public record PasswordResetMessage(
        @NotNull
        String email,

        @NotNull
        String username,

        @NotNull
        String token
) {}

