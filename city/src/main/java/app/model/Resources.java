package app.model;

import jakarta.validation.constraints.NotNull;

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
) {

    public Resources add(Resources other) {
        return new Resources(
                this.barley + other.barley,
                this.clay + other.clay,
                this.bronze + other.bronze,
                this.wood + other.wood,
                this.favor + other.favor,
                this.lapisLazuli + other.lapisLazuli
        );
    }

    public Resources subtract(Resources other) {
        return new Resources(
                this.barley - other.barley,
                this.clay - other.clay,
                this.bronze - other.bronze,
                this.wood - other.wood,
                this.favor - other.favor,
                this.lapisLazuli - other.lapisLazuli
        );
    }

}