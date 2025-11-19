package app.resource;

import app.dto.UserDtos;
import app.model.UserAccount;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class UserAccountResourceTest {

    @BeforeEach
    public void cleanUp() {
        UserAccount.deleteAll();
    }

    @Test
    public void testSignUpSuccess() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("gandalf", "gandalf@middleearth.com", "youhallnotpass");

        given()
                .contentType(ContentType.JSON)
                .body(signup)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    public void testSignUpUsernameAlreadyExists() {
        UserDtos.UserSignup user1 = new UserDtos.UserSignup("aragorn", "strider@ranger.com", "elendil");
        given().contentType(ContentType.JSON).body(user1).post("/api/users/signUp");

        UserDtos.UserSignup user2 = new UserDtos.UserSignup("aragorn", "other@ranger.com", "password123");

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
        UserDtos.UserSignup user1 = new UserDtos.UserSignup("boromir", "gondor@ranger.com", "elendil");
        given().contentType(ContentType.JSON).body(user1).post("/api/users/signUp");

        UserDtos.UserSignup user2 = new UserDtos.UserSignup("faramir", "gondor@ranger.com", "password123");

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
    public void testSignInSuccess() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("legolas", "legolas@woodland.com", "bow123");
        given().contentType(ContentType.JSON).body(signup).post("/api/users/signUp");

        UserDtos.UserLogin login = new UserDtos.UserLogin("legolas", "bow123");

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
        UserDtos.UserSignup signup = new UserDtos.UserSignup("gimli", "gimli@mountain.com", "axe123");
        given().contentType(ContentType.JSON).body(signup).post("/api/users/signUp");

        UserDtos.UserLogin login = new UserDtos.UserLogin("gimli", "wrongpassword");

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/users/signIn")
                .then()
                .statusCode(401);
    }

    @Test
    public void testValidationFailures() {
        UserDtos.UserSignup shortUser = new UserDtos.UserSignup("abc", "valid@email.com", "pass");

        given()
                .contentType(ContentType.JSON)
                .body(shortUser)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);

        UserDtos.UserSignup badEmail = new UserDtos.UserSignup("validUser", "not-an-email", "pass");

        given()
                .contentType(ContentType.JSON)
                .body(badEmail)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);

        UserDtos.UserSignup blankEmail = new UserDtos.UserSignup("validUser2", "", "pass");

        given()
                .contentType(ContentType.JSON)
                .body(blankEmail)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);
    }

    @Test
    public void testSignUpInvalidUsernameCharacters() {
        UserDtos.UserSignup invalidUser = new UserDtos.UserSignup("bad$user", "valid@email.com", "pass");

        given()
                .contentType(ContentType.JSON)
                .body(invalidUser)
                .when()
                .post("/api/users/signUp")
                .then()
                .statusCode(400);
    }

    @Test
    public void testRefreshTokenSuccess() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("frodo", "frodo@shire.com", "ring");
        String token = given()
                .contentType(ContentType.JSON)
                .body(signup)
                .post("/api/users/signUp")
                .jsonPath().getString("token");

        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/users/refresh")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    public void testRefreshTokenUserDeleted() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("samwise", "sam@gamgee.com", "potato");
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
                .statusCode(404);
    }

    @Test
    public void testRefreshTokenUsernameMismatch() {
        UserDtos.UserSignup signup = new UserDtos.UserSignup("smeagol", "my@precious.com", "fish");
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
                .statusCode(404);
    }

    @Test
    public void testAddGoldSuccess() {
        UserAccount user = new UserAccount();
        user.setUsername("bilbo");
        user.setEmail("bilbo@shire.com");
        user.setPassword("smaug");
        user.setGold(10);
        user.persist();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 50)
                .when()
                .post("/api/users/{id}/gold", user.id.toString())
                .then()
                .statusCode(204);

        UserAccount updated = UserAccount.findById(user.id);
        assert updated.getGold() == 60;
    }

    @Test
    public void testAddGoldUserNotFound() {
        String fakeId = new ObjectId().toString();

        given()
                .contentType(ContentType.JSON)
                .queryParam("amount", 50)
                .when()
                .post("/api/users/{id}/gold", fakeId)
                .then()
                .statusCode(404);
    }
}