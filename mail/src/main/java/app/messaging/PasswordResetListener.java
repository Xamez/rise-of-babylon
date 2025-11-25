package app.messaging;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Blocking;
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
    @Blocking
    @Retry(maxRetries = 5, delay = 500, maxDuration = 5000, jitter = 200)
    public Uni<Void> onMessage(JsonObject passwordResetJson) {
        var mail = passwordResetJson.mapTo(PasswordResetMessage.class);
        return mailService.sendResetMail(mail)
                .onItem().invoke(success -> Log.infov("Password reset mail enqueued for {0}", mail.email()))
                .onFailure().invoke(failure -> Log.errorf(failure, "Failed to send reset mail to %s", mail.email()));

    }
}
