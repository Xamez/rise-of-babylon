package app.resource;

import app.dto.CityDtos;
import app.model.City;
import app.service.BuildingService;
import app.service.CityService;
import app.service.DeityService;
import app.service.UnitService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/city")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class CityResource {

    @Inject
    CityService cityService;

    @Inject
    BuildingService buildingService;

    @Inject
    DeityService deityService;

    @Inject
    UnitService unitService;

    @GET
    @Path("/{cityId}")
    @APIResponse(responseCode = "200", description = "Get city by ID")
    public City getCity(@PathParam("cityId") String cityId) {
        return cityService.getCity(cityId);
    }

    @POST
    @Path("/{cityId}/building/upgrade")
    @APIResponse(responseCode = "204", description = "Building upgraded")
    public void upgradeBuilding(@PathParam("cityId") String cityId, @Valid CityDtos.UpgradeBuildingRequest request) {
        buildingService.upgradeBuilding(cityId, request);
    }

    @POST
    @Path("/{cityId}/deity")
    @APIResponse(responseCode = "204", description = "Deity set")
    public void setDeity(@PathParam("cityId") String cityId, @Valid CityDtos.SetDeityRequest request) {
        deityService.setDeity(cityId, request);
    }

    @POST
    @Path("/{cityId}/unit/train")
    @APIResponse(responseCode = "204", description = "Units training started")
    public void trainUnit(@PathParam("cityId") String cityId, @Valid CityDtos.TrainUnitRequest request) {
        unitService.trainUnit(cityId, request);
    }
}
