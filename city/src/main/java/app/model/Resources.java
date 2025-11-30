package app.model;

import io.smallrye.common.constraint.NotNull;

public record Resources(
        @NotNull
        double barley,

        @NotNull
        double clay,

        @NotNull
        double bronze,

        @NotNull
        double wood,

        @NotNull
        double favor,

        @NotNull
        double lapisLazuli
) {}
