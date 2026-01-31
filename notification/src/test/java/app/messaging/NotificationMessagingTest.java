package app.messaging;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class NotificationMessagingTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @Inject
    EventBus eventBus;

    @Test
    void testFullMessagingPipeline() {
        AtomicReference<JsonObject> result = new AtomicReference<>();
        String userId = "user-123";

        eventBus.consumer(NotificationConsumer.PERSONAL_ADDRESS_PREFIX + userId, msg -> {
            result.set((JsonObject) msg.body());
        });

        InMemorySource<JsonObject> source = connector.source("notifications-in");

        JsonObject incomingData = new JsonObject().put("title", "System Alert").put("message", "Example alert").put("severity", "HIGH");
        NotificationMessage notification = new NotificationMessage(
                false,
                userId,
                NotificationType.SYSTEM_ALERT,
                incomingData
        );

        source.send(JsonObject.mapFrom(notification));

        await().untilAsserted(() -> {
            assertNotNull(result.get());
            assertEquals("System Alert", result.get().getString("title"));
        });
    }
}