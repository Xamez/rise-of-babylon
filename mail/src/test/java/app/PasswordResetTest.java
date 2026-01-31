package app;

import app.messaging.PasswordResetMailService;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class PasswordResetTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Inject
    MockMailbox mailbox;

    @InjectMock
    PasswordResetMailService mailService;

    @BeforeEach
    void init() {
        mailbox.clear();
    }

    @Test
    public void testPasswordResetFlowSuccess() {
        JsonObject payload = new JsonObject()
                .put("email", "gandalf@middleearth.com")
                .put("username", "Mithrandir")
                .put("token", "you-shall-not-pass-token");

        connector.source("password-reset").send(payload);

        await().until(() -> mailbox.getTotalMessagesSent() == 1);

        List<Mail> sentMails = mailbox.getMailsSentTo("gandalf@middleearth.com");
        assertEquals(1, sentMails.size());

        Mail mail = sentMails.getFirst();
        assertTrue(mail.getText().contains("Mithrandir"));
        assertTrue(mail.getText().contains("you-shall-not-pass-token"));
    }

    @Test
    public void testPasswordResetFlowFailure() {
        JsonObject payload = new JsonObject()
                .put("email", "gandalf@middleearth.com")
                .put("username", "Mithrandir")
                .put("token", "you-shall-not-pass-token");

        when(mailService.sendResetMail(any())).thenThrow(new RuntimeException("mail send failed"));

        connector.source("password-reset").send(payload);
        await().untilAsserted(() -> verify(mailService, times(6)).sendResetMail(any()));

        assertEquals(0, mailbox.getTotalMessagesSent());
    }
}