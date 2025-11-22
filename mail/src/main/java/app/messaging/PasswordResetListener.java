package app.messaging;

import io.quarkus.logging.Log;
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

    // NOTE: Still issue with channel (do not work until routing key '#' is added...)
    @Incoming("password-reset-in")
    @Blocking
    @Retry(maxRetries = 5, delay = 500, maxDuration = 5000, jitter = 200)
    public void onMessage(JsonObject passwordResetJson) {
        PasswordResetMessage mail = new PasswordResetMessage(
                passwordResetJson.getString("email"),
                passwordResetJson.getString("username"),
                passwordResetJson.getString("token")
        );
        try {
            mailService.sendResetMail(mail);
            Log.infov("Password reset mail enqueued for {0}", mail.email());
        } catch (Exception e) {
            Log.errorf(e, "Failed to send reset mail to %s", mail.email());
            throw e;
        }
    }
}
