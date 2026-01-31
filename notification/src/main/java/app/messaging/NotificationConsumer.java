package app.messaging;

import io.quarkus.logging.Log;
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
    public void onMessage(JsonObject notificationJson) {
        var notification = notificationJson.mapTo(NotificationMessage.class);
        Log.infof("Received notification of type %s (%b)", notification.type(), notification.broadcast());
        if (notification.broadcast()) {
            eventBus.publish(BROADCAST_ADDRESS, notification.data());
            return;
        }
        String userId = notification.userId();
        if (userId != null && !userId.isBlank()) {
            eventBus.publish(PERSONAL_ADDRESS_PREFIX + userId, notification.data());
        }
    }
}
