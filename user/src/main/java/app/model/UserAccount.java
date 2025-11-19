package app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntity(collection = "users")
public class UserAccount extends PanacheMongoEntity {

    @NotNull
    @NotBlank
    @Size(min = 4, max = 35)
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username must contain only alphanumeric characters, underscores and hyphens")
    private String username;

    @NotNull
    @NotBlank
    @Email
    @Schema(description = "User's email address")
    private String email;

    @Schema(description = "Timestamp of the last connection")
    private Instant lastConnectedAt;

    @UpdateTimestamp
    @Schema(description = "Timestamp of the last update")
    private Instant updatedAt;

    @Min(0)
    @Schema(description = "Amount of gold the user has")
    private long gold;

    @NotNull
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
