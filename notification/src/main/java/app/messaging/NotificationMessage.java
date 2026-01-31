package app.messaging;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;

@RegisterForReflection
public record NotificationMessage(
        Boolean broadcast,

        String userId,

        NotificationType type,

        JsonObject data
) {
}
