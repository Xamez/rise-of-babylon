package app.resource;

import app.messaging.NotificationConsumer;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.time.Duration;
import java.util.UUID;

@Path("/stream")
public class NotificationStreamResource {

    @Inject
    EventBus eventBus;

    @Inject
    JsonWebToken jwt;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<JsonObject> stream() {
        String userId = jwt.getSubject();
        Multi<JsonObject> personal = createStream(NotificationConsumer.PERSONAL_ADDRESS_PREFIX + userId);
        Multi<JsonObject> broadcast = createStream(NotificationConsumer.BROADCAST_ADDRESS);
        return Multi.createBy().merging().streams(personal, broadcast);
    }

    private Multi<JsonObject> createStream(String address) {
        return Multi.createFrom().<JsonObject>emitter(emitter -> {
            MessageConsumer<JsonObject> consumer = eventBus.consumer(address, msg -> emitter.emit(decorate(msg.body())));
            emitter.onTermination(consumer::unregister);
        }).runSubscriptionOn(Infrastructure.getDefaultExecutor()).onOverflow().dropPreviousItems();
    }

    private JsonObject decorate(JsonObject payload) {
        JsonObject copy = payload.copy();
        putIfAbsent(copy, "eventId", UUID.randomUUID().toString());
        putIfAbsent(copy, "retry", Duration.ofSeconds(5).toMillis());
        return copy;
    }

    private void putIfAbsent(JsonObject json, String key, Object value) {
        if (!json.containsKey(key)) {
            json.put(key, value);
        }
    }
}
