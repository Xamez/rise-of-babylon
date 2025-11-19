package app.service;

import app.dto.UserDtos;
import app.model.UserAccount;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;

@ApplicationScoped
public class UserAccountService {

    @Inject
    JsonWebToken jwt;

    @Transactional
    public UserDtos.TokenResponse signUp(UserDtos.UserSignup userSignup) {
        if (UserAccount.find("email", userSignup.email()).firstResult() != null) {
            throw new BadRequestException("Email already exists");
        }

        if (UserAccount.find("username", userSignup.username()).firstResult() != null) {
            throw new BadRequestException("Username already exists");
        }

        var userAccount = new UserAccount();
        userAccount.setUsername(userSignup.username());
        userAccount.setEmail(userSignup.email());
        userAccount.setPassword(BcryptUtil.bcryptHash(userSignup.password()));
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
        var token = Jwt.issuer("sumerwars")
                .subject(id.toString())
                .claim("email", email)
                .claim("username", username)
                .sign();
        return new UserDtos.TokenResponse(token);
    }

    @Transactional
    public void addGold(String userId, long amount) {
        UpdateResult result = UserAccount.mongoCollection().updateOne(
                Filters.eq("_id", new ObjectId(userId)),
                Updates.inc("gold", amount)
        );

        if (result.getMatchedCount() == 0) {
            throw new NotFoundException("User not found");
        }
    }

}
