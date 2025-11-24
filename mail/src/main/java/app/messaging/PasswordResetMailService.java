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
        Mail mail = Mail
                .withText(payload.email(), "Password reset",
                        "Hello %s...".formatted(payload.username()) +
                                "\n\nTo reset your password, please click on the following link: " +
                                "\n\nhttps://sumerwars.com/reset-password?token=%s".formatted(payload.token()) +
                                "\n\nIf you did not request a password reset, please ignore this email." +
                                "\n\nBest regards," +
                                "\nThe Sumer Wars Team")
                .setFrom("no-reply@sumerwars.com");

        Log.infof("Sending password reset mail to %s", payload.email());

        return reactiveMailer.send(mail);
    }
}

