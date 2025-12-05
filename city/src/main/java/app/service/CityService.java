package app.service;

import app.dto.CityDtos;
import app.messaging.CityUpdateMessage;
import app.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@ApplicationScoped
public class CityService {

    @Inject
    JsonWebToken jwt;

    @Inject
    @Channel("city-update")
    Emitter<CityUpdateMessage> cityUpdateEmitter;

    public City getCity(String cityId) {
        String userId = jwt.getSubject();
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }
        
        City city = City.findById(new ObjectId(cityId));
        if (city == null) {
            throw new BadRequestException("City not found");
        }
        
        if (!city.getUserId().equals(userId)) {
            throw new BadRequestException("City does not belong to user");
        }
        
        return city;
    }

    public void saveAndNotify(City city) {
        city.setLastUpdated(Instant.now());
        city.update();
        cityUpdateEmitter.send(new CityUpdateMessage(city));
    }

    public City createCity(String userId) {
        City city = new City();
        city.setUserId(userId);
        city.setResources(new Resources(500, 500, 0, 0, 0, 0));
        city.setBuildings(new ArrayList<>());
        city.setUnits(new ArrayList<>());
        city.setBonuses(new ArrayList<>());
        city.setResearches(new ArrayList<>());
        city.setLastUpdated(Instant.now());
        city.persist();
        return city;
    }
}
