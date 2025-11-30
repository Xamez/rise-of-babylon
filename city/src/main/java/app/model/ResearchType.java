package app.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

@Getter
public enum ResearchType {
    // TIER 1
    CANAL_DREDGING(),
    DEEP_MINING(),
    BRICK_FIRING(),

    // TIER 2
    BRONZE_METALLURGY(DEEP_MINING),
    WATERPROOFING(CANAL_DREDGING),
    SIGNAL_FIRE_SYSTEM(BRICK_FIRING),
    ZIGGURAT_ASTRONOMY(BRICK_FIRING),

    // TIER 3
    PHALANX_FORMATION(BRONZE_METALLURGY),
    SLING_AMMUNITION(BRONZE_METALLURGY),
    SPOKED_WHEELS(BRONZE_METALLURGY),
    STANDARDIZED_WEIGHTS(WATERPROOFING),
    MILITARY_CIPHER(SIGNAL_FIRE_SYSTEM),
    DIVINE_CONTRACT(ZIGGURAT_ASTRONOMY),

    // TIER 4
    LARGE_SHIELDS(PHALANX_FORMATION),
    COMPOSITE_BOW(SLING_AMMUNITION),
    REINFORCED_AXLE(SPOKED_WHEELS),
    NAVAL_RAM(WATERPROOFING),
    BORDER_PATROLS(MILITARY_CIPHER),
    CARAVAN_LOGISTICS(STANDARDIZED_WEIGHTS),
    THEOCRACY(ZIGGURAT_ASTRONOMY),
    SCYTHED_BLADES(REINFORCED_AXLE),

    // TIER 5
    DYNASTIC_ADMINISTRATION(THEOCRACY, CARAVAN_LOGISTICS);

    private final Set<ResearchType> requiredTechs;

    ResearchType(ResearchType... requiredTechs) {
        this.requiredTechs = Set.of(requiredTechs);
    }

    public boolean isUnlockable(Set<ResearchType> researchedTechs) {
        if (this.requiredTechs == null || this.requiredTechs.isEmpty())
            return true;

        return researchedTechs.containsAll(this.requiredTechs);
    }
}