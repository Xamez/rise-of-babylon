package app.resource;

import app.dto.UserDtos;
import app.service.UserAccountService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserAccountResource {

    @Inject
    UserAccountService userAccountService;

    @PermitAll
    @POST
    @Path("/signUp")
    @APIResponse(responseCode = "200", description = "User created and token issued")
    @APIResponse(responseCode = "400", description = "Username or Email already exists")
    public UserDtos.TokenResponse signUp(@Valid UserDtos.UserSignup userSignup) {
        return userAccountService.signUp(userSignup);
    }

    @PermitAll
    @POST
    @Path("/signIn")
    @APIResponse(responseCode = "200", description = "Sign in successful")
    @APIResponse(responseCode = "401", description = "Invalid credentials")
    public UserDtos.TokenResponse signIn(@Valid UserDtos.UserLogin userLogin) {
        return userAccountService.signIn(userLogin);
    }

    @Authenticated
    @POST
    @Path("/refresh")
    @APIResponse(responseCode = "200", description = "Token refreshed")
    @APIResponse(responseCode = "401", description = "Invalid or expired token")
    public UserDtos.TokenResponse refreshToken() {
        return userAccountService.refreshToken();
    }

    @PermitAll
    @POST
    @Path("/{id}/gold")
    @APIResponse(responseCode = "204", description = "Gold added successfully")
    @APIResponse(responseCode = "404", description = "User not found")
    public void addGold(@PathParam("id") String id, @QueryParam("amount") @Min(0) long amount) {
        userAccountService.addGold(id, amount);
    }

}
