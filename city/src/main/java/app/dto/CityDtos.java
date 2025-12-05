package app.dto;

import app.model.BuildingType;
import app.model.DeityType;
import app.model.UnitType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CityDtos {

    public record UpgradeBuildingRequest(
            @NotNull
            BuildingType type
    ) {}

    public record SetDeityRequest(
            @NotNull
            DeityType deity
    ) {}

    public record TrainUnitRequest(
            @NotNull
            UnitType type,

            @Min(1)
            int count
    ) {}
}
