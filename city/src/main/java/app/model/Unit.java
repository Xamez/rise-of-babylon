package app.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Unit(
        @NotNull
        UnitType type,

        @Min(0)
        int count
) {}
