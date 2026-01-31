package app.resource;

import app.messaging.NotificationConsumer;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Multi;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.time.Duration;

@Path("/stream")
@Authenticated
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
        Multi<JsonObject> ping = Multi.createFrom().ticks().every(Duration.ofSeconds(30))
                .onOverflow().drop().map(tick -> new JsonObject().put("type", "ping"));
        return Multi.createBy().merging().streams(personal, broadcast, ping);
    }

    private Multi<JsonObject> createStream(String address) {
        return Multi.createFrom().<JsonObject>emitter(emitter -> {
            MessageConsumer<JsonObject> consumer = eventBus.consumer(address, msg -> {
                if (!emitter.isCancelled()) {
                    emitter.emit(msg.body());
                }
            });
            emitter.onTermination(consumer::unregister);
        }).onOverflow().buffer(250);
    }
}
