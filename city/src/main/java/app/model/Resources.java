package app.model;

import io.smallrye.common.constraint.NotNull;

public record Resources(
        @NotNull
        double wheat,

        @NotNull
        double clay,

        @NotNull
        double copper,

        @NotNull
        double wood,

        @NotNull
        double lapisLazuli,

        @NotNull
        double tears
) {
}
