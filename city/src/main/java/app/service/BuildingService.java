package app.service;

import app.model.BuildingType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BuildingService {

    public double getHourlyProduction(BuildingType type, int level) {
        if (level == 0) return 0;

        return switch (type) {
            case BARLEY_FIELDS -> 10 * Math.pow(1.2, level);
            default -> 0;
        };
    }

    public double getStorageCapacity(int warehouseLevel) {
        return 500 * Math.pow(1.3, warehouseLevel);
    }
}
