package app.service;

import app.dto.CityDtos;
import app.model.BuildingType;
import app.model.City;
import app.model.Resources;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class DeityService {

    @Inject
    CityService cityService;

    public void setDeity(String cityId, CityDtos.SetDeityRequest request) {
        City city = cityService.getCity(cityId);
        if (!city.hasBuilding(BuildingType.ZIGGURAT)) {
            throw new BadRequestException("City must have a Ziggurat to set a deity.");
        }

        Resources current = city.getResources();

        city.setDeity(request.deity());
        city.setResources(new Resources(
                current.barley(),
                current.clay(),
                current.bronze(),
                current.wood(),
                0,
                current.lapisLazuli()
        ));

        cityService.saveAndNotify(city);
    }
}
