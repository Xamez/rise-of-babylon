package app.messaging;

import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PasswordResetMailService {

    @Inject
    ReactiveMailer reactiveMailer;

    public Uni<Void> sendResetMail(PasswordResetMessage payload) {
        var text =
                """
                        Hello %s...
                       \s
                        To reset your password, please click on the following link:\s
                       \s
                        https://sumerwars.com/reset-password?token=%s
                       \s
                        If you did not request a password reset, please ignore this email.
                       \s
                        Best regards,
                        The Sumer Wars Team
                       \s""".formatted(payload.username(), payload.token());
        Mail mail = Mail
                .withText(payload.email(), "Password reset", text)
                .setFrom("no-reply@sumerwars.com");

        Log.infof("Sending password reset mail to %s", payload.email());

        return reactiveMailer.send(mail);
    }
}

