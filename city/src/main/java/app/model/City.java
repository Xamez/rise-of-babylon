package app.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntity(collection = "cities")
public class City extends PanacheMongoEntity {
    @NotNull
    @NotBlank
    private UUID playerId;

    @NotNull
    private Resources resources;

    @NotNull
    @UpdateTimestamp
    private Instant lastUpdated;

    @NotNull
    private Buildings buildings;

    @NotNull
    private List<Bonus> bonuses;

    @NotNull
    private List<ResearchType> researches;
}
