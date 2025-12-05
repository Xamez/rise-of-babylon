package app.service;

import app.dto.CityDtos;
import app.model.Building;
import app.model.BuildingType;
import app.model.City;
import app.model.Resources;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class BuildingService {

    @Inject
    CityService cityService;

    private static final Map<BuildingType, Resources> BASE_COSTS = Map.ofEntries(
            Map.entry(BuildingType.BARLEY_FIELDS, new Resources(0, 50, 0, 0, 0, 0)),
            Map.entry(BuildingType.CLAY_PIT, new Resources(50, 0, 0, 0, 0, 0)),
            Map.entry(BuildingType.BRONZE_FOUNDRY, new Resources(50, 100, 0, 0, 0, 0)),
            Map.entry(BuildingType.WAREHOUSE, new Resources(0, 200, 0, 100, 0, 0)),
            Map.entry(BuildingType.BARRACKS, new Resources(0, 200, 100, 0, 0, 0)),
            Map.entry(BuildingType.RIVER_SHIPYARD, new Resources(0, 150, 0, 200, 0, 0)),
            Map.entry(BuildingType.CITY_WALLS, new Resources(0, 500, 0, 0, 0, 0)),
            Map.entry(BuildingType.WATCHTOWER, new Resources(0, 300, 0, 50, 0, 0)),
            Map.entry(BuildingType.DWELLING, new Resources(20, 80, 0, 0, 0, 0)),
            Map.entry(BuildingType.SOUK, new Resources(50, 150, 0, 0, 0, 0)),
            Map.entry(BuildingType.ZIGGURAT, new Resources(0, 1000, 200, 0, 0, 0)),
            Map.entry(BuildingType.TABLET_HOUSE, new Resources(0, 400, 0, 100, 0, 0)),
            Map.entry(BuildingType.ROYAL_PALACE, new Resources(0, 0, 0, 0, 0, 0))
    );

    public void upgradeBuilding(String cityId, CityDtos.UpgradeBuildingRequest request) {
        City city = cityService.getCity(cityId);

        Optional<Building> buildingOpt = city.getBuildings().stream()
                .filter(b -> b.type() == request.type())
                .findFirst();

        int currentLevel = buildingOpt.map(Building::level).orElse(0);
        int nextLevel = currentLevel + 1;

        Resources cost = calculateCost(request.type(), nextLevel);
        deductResources(city, cost);

        buildingOpt.ifPresent(building -> city.getBuildings().remove(building));
        city.getBuildings().add(new Building(request.type(), nextLevel));

        cityService.saveAndNotify(city);
    }

    // TODO: Calcul is probably wrong, need to verify with game design
    private Resources calculateCost(BuildingType type, int level) {
        Resources base = BASE_COSTS.getOrDefault(type, new Resources(0, 0, 0, 0, 0, 0));
        double multiplier = Math.pow(1.2, level - 1);

        return new Resources(
                base.barley() * multiplier,
                base.clay() * multiplier,
                base.bronze() * multiplier,
                base.wood() * multiplier,
                base.favor() * multiplier,
                base.lapisLazuli() * multiplier
        );
    }

    private void deductResources(City city, Resources cost) {
        Resources current = city.getResources();

        if (current.barley() < cost.barley() ||
            current.clay() < cost.clay() ||
            current.bronze() < cost.bronze() ||
            current.wood() < cost.wood() ||
            current.favor() < cost.favor() ||
            current.lapisLazuli() < cost.lapisLazuli()) {
            throw new BadRequestException("Not enough resources");
        }

        city.setResources(current.subtract(cost));
    }

}
