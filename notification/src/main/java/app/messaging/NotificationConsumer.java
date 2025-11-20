package app.messaging;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class NotificationConsumer {

    public static final String BROADCAST_ADDRESS = "notifications.broadcast";
    public static final String PERSONAL_ADDRESS_PREFIX = "notifications.user.";

    @Inject
    EventBus eventBus;

    @Incoming("notifications-in")
    public void onMessage(JsonObject raw) {
        if (raw.getBoolean("broadcast", false)) {
            eventBus.publish(BROADCAST_ADDRESS, raw);
            return;
        }
        String userId = raw.getString("userId");
        if (userId != null && !userId.isBlank()) {
            eventBus.publish(PERSONAL_ADDRESS_PREFIX + userId, raw);
        }
    }
}
