package app.messaging;

import app.model.City;

public record CityUpdateMessage(
        City city
) {}
