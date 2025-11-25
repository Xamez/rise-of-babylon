package app.model;

import jakarta.validation.constraints.NotNull;

public record Building(
        @NotNull
        BuildingType type,

        @NotNull
        int level
) {
}
