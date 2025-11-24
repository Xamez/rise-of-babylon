package app.resource;

import app.dto.UserDtos;
import app.messaging.PasswordResetMessage;
import app.model.UserAccount;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class UserAccountResourceTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @BeforeEach
    public void cleanUp() {
        UserAccount.deleteAll();
        connector.sink("password-reset").clear();
    }

    @Test
    public void testSignUpSuccess() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("gandalf", "gandalf@middleearth.com", "Password1");

        given()
                .contentType(ContentType.JSON)
                .body(signup)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(200)
                .body("token", notNullValue());

        UserAccount user = UserAccount.find("username", "gandalf").firstResult();
        assertNotNull(user);
        assertEquals("gandalf", user.getUsername());
        assertEquals("gandalf@middleearth.com", user.getEmail());
        assertEquals(0, user.getGold());
        assertNotNull(user.getLastConnectedAt());
    }

    @Test
    public void testSignUpUsernameAlreadyExists() {
        UserDtos.UserSignup user1 = new UserDtos.UserSignup("aragorn", "strider@ranger.com", "Elendil1");
        given().contentType(ContentType.JSON).body(user1).post("/api/users/signUp");

        UserDtos.UserSignup user2 = new UserDtos.UserSignup("aragorn", "other@ranger.com", "Password123");

        given()
                .contentType(ContentType.JSON)
                .body(user2)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400)
                .body("message", containsString("Username already exists"));
    }

    @Test
    public void testSignUpEmailAlreadyExists() {
        UserDtos.UserSignup user1 = new UserDtos.UserSignup("boromir", "gondor@ranger.com", "Elendil1");
        given().contentType(ContentType.JSON).body(user1).post("/api/users/signUp");

        UserDtos.UserSignup user2 = new UserDtos.UserSignup("faramir", "gondor@ranger.com", "Password123");

        given()
                .contentType(ContentType.JSON)
                .body(user2)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400)
                .body("message", containsString("Email already exists"));
    }

    @Test
    public void testSignUpPasswordPolicyViolation() {
        // No uppercase
        UserDtos.UserSignup noUppercase = new UserDtos.UserSignup("user1", "user1@test.com", "password1");
        given()
                .contentType(ContentType.JSON)
                .body(noUppercase)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400)
                .body("violations.message", hasItem(containsString("Password must be at least 6 characters")));

        // No number
        UserDtos.UserSignup noNumber = new UserDtos.UserSignup("user2", "user2@test.com", "Password");
        given()
                .contentType(ContentType.JSON)
                .body(noNumber)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400)
                .body("violations.message", hasItem(containsString("Password must be at least 6 characters")));

        // Too short
        UserDtos.UserSignup tooShort = new UserDtos.UserSignup("user3", "user3@test.com", "Pa1");
        given()
                .contentType(ContentType.JSON)
                .body(tooShort)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);
    }

    @Test
    public void testSignUpValidationFailures() {
        // Username too short
        UserDtos.UserSignup shortUser = new UserDtos.UserSignup("abc", "valid@email.com", "Password1");
        given()
                .contentType(ContentType.JSON)
                .body(shortUser)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);

        // Invalid email
        UserDtos.UserSignup badEmail = new UserDtos.UserSignup("validUser", "not-an-email", "Password1");
        given()
                .contentType(ContentType.JSON)
                .body(badEmail)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);

        // Blank email
        UserDtos.UserSignup blankEmail = new UserDtos.UserSignup("validUser2", "", "Password1");
        given()
                .contentType(ContentType.JSON)
                .body(blankEmail)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);

        // Username too long
        UserDtos.UserSignup longUsername = new UserDtos.UserSignup("a".repeat(36), "valid@email.com", "Password1");
        given()
                .contentType(ContentType.JSON)
                .body(longUsername)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);
    }

    @Test
    public void testSignUpInvalidUsernameCharacters() {
        UserDtos.UserSignup invalidUser = new UserDtos.UserSignup("bad$user", "valid@email.com", "Password1");

        given()
                .contentType(ContentType.JSON)
                .body(invalidUser)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);
    }

    @Test
    public void testSignInSuccess() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("legolas", "legolas@woodland.com", "Bow1234");
        given().contentType(ContentType.JSON).body(signup).post("/api/users/signUp");

        UserDtos.UserLogin login = new UserDtos.UserLogin("legolas", "Bow1234");

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/users/signIn")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    public void testSignInWrongPassword() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("gimli", "gimli@mountain.com", "Axe1234");
        given().contentType(ContentType.JSON).body(signup).post("/api/users/signUp");

        UserDtos.UserLogin login = new UserDtos.UserLogin("gimli", "wrongpassword");

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/users/signIn")
                .then()
                .statusCode(401)
                .body("message", containsString("Invalid username or password"));
    }

    @Test
    public void testSignInUserNotFound() {
        UserDtos.UserLogin login = new UserDtos.UserLogin("nonexistent", "Password1");

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/users/signIn")
                .then()
                .statusCode(401)
                .body("message", containsString("Invalid username or password"));
    }

    @Test
    public void testRefreshTokenSuccess() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("frodo", "frodo@shire.com", "Ring123");
        String token = given()
                .contentType(ContentType.JSON)
                .body(signup)
                .post("/api/users/signUp")
                .jsonPath().getString("token");

        String newToken = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/refresh")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().path("token");

        assertNotNull(newToken);
        assertNotEquals(token, newToken);
    }

    @Test
    public void testRefreshTokenUnauthorized() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/refresh")
                .then()
                .statusCode(401);
    }

    @Test
    public void testRefreshTokenUserDeleted() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("samwise", "sam@gamgee.com", "Potato1");
        String token = given()
                .contentType(ContentType.JSON)
                .body(signup)
                .post("/api/users/signUp")
                .jsonPath().getString("token");

        UserAccount.deleteAll();

        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/refresh")
                .then()
                .statusCode(404)
                .body("message", containsString("User not found"));
    }

    @Test
    public void testRefreshTokenUsernameMismatch() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("smeagol", "my@precious.com", "Fish123");
        String token = given()
                .contentType(ContentType.JSON)
                .body(signup)
                .post("/api/users/signUp")
                .jsonPath().getString("token");

        UserAccount user = UserAccount.find("username", "smeagol").firstResult();
        user.setUsername("gollum");
        user.update();

        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/refresh")
                .then()
                .statusCode(404)
                .body("message", containsString("User not found"));
    }

    @Test
    public void testAddGoldSuccess() {
        UserAccount user = new UserAccount();
        user.setUsername("bilbo");
        user.setEmail("bilbo@shire.com");
        user.setPassword("Smaug123");
        user.setGold(10);
        user.persist();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 50)
                .when()
                .post("/api/users/{id}/gold/add", user.id.toString())
                .then()
                .statusCode(204);

        UserAccount updated = UserAccount.findById(user.id);
        assertEquals(60, updated.getGold());
    }

    @Test
    public void testAddGoldUserNotFound() {
        String fakeId = new ObjectId().toString();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 50)
                .when()
                .post("/api/users/{id}/gold/add", fakeId)
                .then()
                .statusCode(400)
                .body("message", containsString("Unable to add gold to user"));
    }

    @Test
    public void testAddGoldInvalidUserId() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 50)
                .when()
                .post("/api/users/{id}/gold/add", "invalid-id")
                .then()
                .statusCode(400)
                .body("message", containsString("Invalid user ID format"));
    }

    @Test
    public void testAddGoldInvalidAmount() {
        UserAccount user = new UserAccount();
        user.setUsername("thorin");
        user.setEmail("thorin@erebor.com");
        user.setPassword("Mountain1");
        user.setGold(100);
        user.persist();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 0)
                .when()
                .post("/api/users/{id}/gold/add", user.id.toString())
                .then()
                .statusCode(400);

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", -10)
                .when()
                .post("/api/users/{id}/gold/add", user.id.toString())
                .then()
                .statusCode(400);
    }

    @Test
    public void testRemoveGoldSuccess() {
        UserAccount user = new UserAccount();
        user.setUsername("thranduil");
        user.setEmail("thranduil@woodland.com");
        user.setPassword("Elven123");
        user.setGold(100);
        user.persist();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 30)
                .when()
                .post("/api/users/{id}/gold/remove", user.id.toString())
                .then()
                .statusCode(204);

        UserAccount updated = UserAccount.findById(user.id);
        assertEquals(70, updated.getGold());
    }

    @Test
    public void testRemoveGoldInsufficientBalance() {
        UserAccount user = new UserAccount();
        user.setUsername("bard");
        user.setEmail("bard@laketown.com");
        user.setPassword("Arrow123");
        user.setGold(10);
        user.persist();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 50)
                .when()
                .post("/api/users/{id}/gold/remove", user.id.toString())
                .then()
                .statusCode(400)
                .body("message", containsString("Unable to remove gold from user"));
    }

    @Test
    public void testRemoveGoldUserNotFound() {
        String fakeId = new ObjectId().toString();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 10)
                .when()
                .post("/api/users/{id}/gold/remove", fakeId)
                .then()
                .statusCode(400)
                .body("message", containsString("Unable to remove gold from user"));
    }

    @Test
    public void testRemoveGoldInvalidUserId() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 10)
                .when()
                .post("/api/users/{id}/gold/remove", "invalid-id")
                .then()
                .statusCode(400)
                .body("message", containsString("Invalid user ID format"));
    }

    @Test
    public void testRemoveGoldInvalidAmount() {
        UserAccount user = new UserAccount();
        user.setUsername("dain");
        user.setEmail("dain@ironhills.com");
        user.setPassword("IronFist1");
        user.setGold(100);
        user.persist();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 0)
                .when()
                .post("/api/users/{id}/gold/remove", user.id.toString())
                .then()
                .statusCode(400);
    }

    @Test
    public void testPasswordResetRequestSuccess() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("elrond", "elrond@rivendell.com", "Rivendell1");
        String token = given()
                .contentType(ContentType.JSON)
                .body(signup)
                .post("/api/users/signUp")
                .jsonPath().getString("token");

        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/password/reset-request")
                .then()
                .statusCode(204);

        // Verify user has reset token
        UserAccount user = UserAccount.find("email", "elrond@rivendell.com").firstResult();
        assertNotNull(user.getPasswordResetToken());
        assertNotNull(user.getPasswordResetTokenExpiresAt());
        assertTrue(user.getPasswordResetTokenExpiresAt().isAfter(Instant.now()));

        // Verify message was sent
        InMemorySink<PasswordResetMessage> sink = connector.sink("password-reset");
        assertEquals(1, sink.received().size());
        PasswordResetMessage message = sink.received().get(0).getPayload();
        assertEquals("elrond@rivendell.com", message.email());
        assertEquals("elrond", message.username());
        assertNotNull(message.token());
    }

    @Test
    public void testPasswordResetRequestUnauthorized() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/password/reset-request")
                .then()
                .statusCode(401);
    }

    @Test
    public void testPasswordResetRequestUserNotFound() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("celeborn", "celeborn@lothlorien.com", "Galadriel1");
        String token = given()
                .contentType(ContentType.JSON)
                .body(signup)
                .post("/api/users/signUp")
                .jsonPath().getString("token");

        UserAccount.deleteAll();

        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/password/reset-request")
                .then()
                .statusCode(404);
    }

    @Test
    public void testPasswordResetCompleteSuccess() {
        // Create user and request reset
        UserAccount user = new UserAccount();
        user.setUsername("arwen");
        user.setEmail("arwen@rivendell.com");
        user.setPassword("OldPassword1");
        user.setPasswordResetToken("valid-token-123");
        user.setPasswordResetTokenExpiresAt(Instant.now().plusSeconds(1800));
        user.persist();

        UserDtos.PasswordResetConfirm confirm = new UserDtos.PasswordResetConfirm("valid-token-123", "NewPassword1");

        given()
                .contentType(ContentType.JSON)
                .body(confirm)
                .when()
                .post("/api/users/password/reset")
                .then()
                .statusCode(204);

        // Verify token is cleared
        UserAccount updated = UserAccount.findById(user.id);
        assertNull(updated.getPasswordResetToken());
        assertNull(updated.getPasswordResetTokenExpiresAt());

        // Verify new password works
        UserDtos.UserLogin login = new UserDtos.UserLogin("arwen", "NewPassword1");
        given()
                .contentType(ContentType.JSON)
                .body(login)
                .post("/api/users/signIn")
                .then()
                .statusCode(200);
    }

    @Test
    public void testPasswordResetCompleteInvalidToken() {
        UserDtos.PasswordResetConfirm confirm = new UserDtos.PasswordResetConfirm("invalid-token", "NewPassword1");

        given()
                .contentType(ContentType.JSON)
                .body(confirm)
                .when()
                .post("/api/users/password/reset")
                .then()
                .statusCode(404)
                .body("message", containsString("Invalid token"));
    }

    @Test
    public void testPasswordResetCompleteExpiredToken() {
        UserAccount user = new UserAccount();
        user.setUsername("galadriel");
        user.setEmail("galadriel@lothlorien.com");
        user.setPassword("OldPassword1");
        user.setPasswordResetToken("expired-token");
        user.setPasswordResetTokenExpiresAt(Instant.now().minusSeconds(60));
        user.persist();

        UserDtos.PasswordResetConfirm confirm = new UserDtos.PasswordResetConfirm("expired-token", "NewPassword1");

        given()
                .contentType(ContentType.JSON)
                .body(confirm)
                .when()
                .post("/api/users/password/reset")
                .then()
                .statusCode(400)
                .body("message", containsString("Token expired"));
    }

    @Test
    public void testPasswordResetCompleteWeakPassword() {
        UserAccount user = new UserAccount();
        user.setUsername("saruman");
        user.setEmail("saruman@isengard.com");
        user.setPassword("OldPassword1");
        user.setPasswordResetToken("valid-token");
        user.setPasswordResetTokenExpiresAt(Instant.now().plusSeconds(1800));
        user.persist();

        // No uppercase
        UserDtos.PasswordResetConfirm weakPassword1 = new UserDtos.PasswordResetConfirm("valid-token", "password1");
        given()
                .contentType(ContentType.JSON)
                .body(weakPassword1)
                .when()
                .post("/api/users/password/reset")
                .then()
                .statusCode(400)
                .body("violations.message", hasItem(containsString("Password must be at least 6 characters")));

        // No number
        UserDtos.PasswordResetConfirm weakPassword2 = new UserDtos.PasswordResetConfirm("valid-token", "Password");
        given()
                .contentType(ContentType.JSON)
                .body(weakPassword2)
                .when()
                .post("/api/users/password/reset")
                .then()
                .statusCode(400)
                .body("violations.message", hasItem(containsString("Password must be at least 6 characters")));

        // Too short
        UserDtos.PasswordResetConfirm weakPassword3 = new UserDtos.PasswordResetConfirm("valid-token", "Pa1");
        given()
                .contentType(ContentType.JSON)
                .body(weakPassword3)
                .when()
                .post("/api/users/password/reset")
                .then()
                .statusCode(400);
    }

    @Test
    public void testPasswordResetCompleteBlankToken() {
        UserDtos.PasswordResetConfirm confirm = new UserDtos.PasswordResetConfirm("", "NewPassword1");

        given()
                .contentType(ContentType.JSON)
                .body(confirm)
                .when()
                .post("/api/users/password/reset")
                .then()
                .statusCode(400);
    }
}
