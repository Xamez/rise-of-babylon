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

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntity(collection = "cities")
public class City extends PanacheMongoEntity {
    @NotNull
    @NotBlank
    private String userId;

    @NotNull
    private Resources resources;

    @NotNull
    @UpdateTimestamp
    private Instant lastUpdated;

    @NotNull
    private List<Building> buildings;

    @NotNull
    private List<Unit> units;

    private DeityType deity;

    @NotNull
    private List<Bonus> bonuses;

    @NotNull
    private List<ResearchType> researches;

    public boolean hasBuilding(BuildingType type) {
        return buildings.stream().anyMatch(b -> b.type() == type);
    }

    public boolean hasBuildingAtLevel(BuildingType type, int level) {
        return buildings.stream().anyMatch(b -> b.type() == type && b.level() >= level);
    }

}
