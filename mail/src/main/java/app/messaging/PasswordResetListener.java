package app.messaging;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class PasswordResetListener {

    @Inject
    PasswordResetMailService mailService;

    @Incoming("password-reset")
    @Retry(maxRetries = 5, delay = 500, maxDuration = 5000, jitter = 200)
    public Uni<Void> onMessage(JsonObject passwordResetJson) {
        PasswordResetMessage payload = passwordResetJson.mapTo(PasswordResetMessage.class);
        Log.infof("Received password reset request for %s", payload.email());
        return mailService.sendResetMail(payload)
                .onItem().invoke(success -> Log.infov("Password reset mail enqueued for {0}", payload.email()))
                .onFailure().invoke(failure -> Log.errorf(failure, "Failed to send reset mail to %s", payload.email()));

    }
}