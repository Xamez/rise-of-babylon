package app.service;

import app.dto.CityDtos;
import app.model.City;
import app.model.Resources;
import app.model.Unit;
import app.model.UnitType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UnitService {

    @Inject
    CityService cityService;

    private static final Map<UnitType, Resources> UNIT_COSTS = Map.ofEntries(
            Map.entry(UnitType.SLINGER, new Resources(40, 60, 0, 0, 0, 0)),
            Map.entry(UnitType.SPEARMAN, new Resources(80, 0, 40, 0, 0, 0)),
            Map.entry(UnitType.AXEMAN, new Resources(120, 0, 80, 0, 0, 0)),
            Map.entry(UnitType.ROYAL_GUARD, new Resources(300, 0, 200, 0, 0, 0)),
            Map.entry(UnitType.ARCHER, new Resources(100, 0, 0, 50, 0, 0)),
            Map.entry(UnitType.WAR_CART, new Resources(200, 0, 150, 0, 0, 0)),
            Map.entry(UnitType.CHARIOT, new Resources(400, 0, 0, 200, 0, 0)),
            Map.entry(UnitType.LIGHT_BARQUE, new Resources(0, 100, 0, 50, 0, 0)),
            Map.entry(UnitType.TRANSPORT_BARGE, new Resources(0, 300, 0, 200, 0, 0)),
            Map.entry(UnitType.WAR_BARGE, new Resources(0, 200, 100, 150, 0, 0)),
            Map.entry(UnitType.ASSAULT_SHIP, new Resources(0, 400, 200, 300, 0, 0)),
            Map.entry(UnitType.INFORMANT, new Resources(200, 0, 0, 0, 0, 0)),
            Map.entry(UnitType.SETTLER_CARAVAN, new Resources(10000, 10000, 0, 5000, 0, 0)),
            Map.entry(UnitType.ZIGGURAT_GUARDIAN, new Resources(0, 500, 300, 0, 50, 0)),
            Map.entry(UnitType.LAMASSU, new Resources(1000, 0, 0, 0, 400, 0)),
            Map.entry(UnitType.SCORPION_MAN, new Resources(1000, 0, 0, 0, 350, 0)),
            Map.entry(UnitType.PAZUZU, new Resources(1000, 0, 0, 0, 300, 0))
    );

    public void trainUnit(String cityId, CityDtos.TrainUnitRequest request) {
        City city = cityService.getCity(cityId);

        Resources unitCost = UNIT_COSTS.getOrDefault(request.type(), new Resources(0, 0, 0, 0, 0, 0));
        Resources totalCost = new Resources(
                unitCost.barley() * request.count(),
                unitCost.clay() * request.count(),
                unitCost.bronze() * request.count(),
                unitCost.wood() * request.count(),
                unitCost.favor() * request.count(),
                unitCost.lapisLazuli() * request.count()
        );

        deductResources(city, totalCost);

        Optional<Unit> unitOpt = city.getUnits().stream()
                .filter(u -> u.type() == request.type())
                .findFirst();

        int currentCount = unitOpt.map(Unit::count).orElse(0);

        unitOpt.ifPresent(unit -> city.getUnits().remove(unit));
        city.getUnits().add(new Unit(request.type(), currentCount + request.count()));

        cityService.saveAndNotify(city);
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
