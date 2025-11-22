package app.messaging;

import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PasswordResetMailService {

    @Inject
    Mailer mailer;

    public void sendResetMail(PasswordResetMessage payload) {
        Mail mail = Mail
                .withText(payload.email(), "Password reset",
                        "Hello %s,%n%nUse the following link to reset your password: %s%nThis link expires in 30 minutes.".formatted(
                                payload.username(), "URL_PLACEHOLDER?token=" + payload.token()))
                .setFrom("no-reply@sumerwars.com");
        Log.infof("Sending password reset mail to %s", payload.email());
        // TODO: Investigate why send mail is so slow
        mailer.send(mail);
    }
}

