package app.service;

import app.dto.UserDtos;
import app.dto.UserDtos.PasswordResetConfirm;
import app.messaging.PasswordResetMessage;
import app.model.UserAccount;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.logging.Log;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import static app.dto.UserDtos.PASSWORD_POLICY_MESSAGE;
import static app.dto.UserDtos.PASSWORD_POLICY_REGEX;

@ApplicationScoped
public class UserAccountService {

    @Inject
    JsonWebToken jwt;

    @Inject
    @Channel("password-reset")
    Emitter<PasswordResetMessage> passwordResetEmitter;

    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(30);

    @Transactional
    public UserDtos.TokenResponse signUp(UserDtos.UserSignup userSignup) {
        if (UserAccount.find("email", userSignup.email()).firstResult() != null) {
            throw new BadRequestException("Email already exists");
        }

        if (UserAccount.find("username", userSignup.username()).firstResult() != null) {
            throw new BadRequestException("Username already exists");
        }

        if (!userSignup.password().matches(PASSWORD_POLICY_REGEX)) {
            throw new BadRequestException(PASSWORD_POLICY_MESSAGE);
        }

        var userAccount = new UserAccount();
        userAccount.setUsername(userSignup.username());
        userAccount.setEmail(userSignup.email());
        userAccount.setPassword(hashPassword(userSignup.password()));
        userAccount.setLastConnectedAt(Instant.now());
        userAccount.setGold(0);
        userAccount.persist();

        return generateToken(userAccount.id, userAccount.getEmail(), userAccount.getUsername());
    }

    public UserDtos.TokenResponse signIn(UserDtos.UserLogin userLogin) {
        UserAccount userAccount = UserAccount.find("username", userLogin.username()).firstResult();
        if (userAccount == null || !BcryptUtil.matches(userLogin.password(), userAccount.getPassword())) {
            throw new NotAuthorizedException("Invalid username or password");
        }

        return generateToken(userAccount.id, userAccount.getEmail(), userAccount.getUsername());
    }

    public UserDtos.TokenResponse refreshToken() {
        String subject = jwt.getSubject();
        String email = jwt.getClaim("email");
        String username = jwt.getClaim("username");

        ObjectId id = new ObjectId(subject);

        if (UserAccount.find("email", email).firstResult() == null) {
            throw new NotFoundException("User not found");
        }

        if (UserAccount.find("username", username).firstResult() == null) {
            throw new NotFoundException("User not found");
        }

        return generateToken(id, email, username);
    }

    private UserDtos.TokenResponse generateToken(ObjectId id, String email, String username) {
        var token = Jwt.issuer("https://sumerwars.com")
                .subject(id.toString())
                .claim("email", email)
                .claim("username", username)
                .sign();
        return new UserDtos.TokenResponse(token);
    }

    @Transactional
    public void addGold(String userId, long amount) {
        if (!ObjectId.isValid(userId)) {
            throw new BadRequestException("Invalid user ID format");
        }

        UpdateResult result = UserAccount.mongoCollection().updateOne(
                Filters.eq("_id", new ObjectId(userId)),
                Updates.inc("gold", amount)
        );

        if (result.getMatchedCount() == 0) {
            throw new BadRequestException("Unable to add gold to user");
        }
        Log.infov("Added {0} gold to user {1}", amount, userId);
    }

    @Transactional
    public void removeGold(String userId, long amount) {
        if (!ObjectId.isValid(userId)) {
            throw new BadRequestException("Invalid user ID format");
        }

        UpdateResult result = UserAccount.mongoCollection().updateOne(
                Filters.and(
                        Filters.eq("_id", new ObjectId(userId)),
                        Filters.gte("gold", amount)
                ),
                Updates.inc("gold", -amount)
        );

        if (result.getMatchedCount() == 0) {
            throw new BadRequestException("Unable to remove gold from user");
        }
        Log.infov("Removed {0} gold from user {1}", amount, userId);
    }

    @Transactional
    public void initiatePasswordReset() {
        String email = jwt.getClaim("email");
        UserAccount user = UserAccount.find("email", email).firstResult();
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        String token = generateSecureToken();
        Instant expiresAt = Instant.now().plus(RESET_TOKEN_TTL);
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(expiresAt);
        user.persistOrUpdate();
        Log.infov("Password reset requested for user {0}", user.getEmail());
        try {
            passwordResetEmitter.send(new PasswordResetMessage(user.getEmail(), user.getUsername(), token));
        } catch (Exception e) {
            Log.error("Failed to publish password reset message", e);
            throw new RuntimeException("Unable to send password reset email");
        }
        Log.infov("Password reset token published for user {0}", user.getEmail());
    }

    @Transactional
    public void completePasswordReset(PasswordResetConfirm confirm) {
        UserAccount user = UserAccount.find("passwordResetToken", confirm.token()).firstResult();
        if (user == null) {
            throw new NotFoundException("Invalid token");
        }
        if (user.getPasswordResetTokenExpiresAt() == null || user.getPasswordResetTokenExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Token expired");
        }
        if (!confirm.newPassword().matches(PASSWORD_POLICY_REGEX)) {
            throw new BadRequestException(PASSWORD_POLICY_MESSAGE);
        }
        user.setPassword(hashPassword(confirm.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        user.persistOrUpdate();
        Log.infov("Password reset completed for user {0}", user.getEmail());
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashPassword(String password) {
        return BcryptUtil.bcryptHash(password);
    }

}
