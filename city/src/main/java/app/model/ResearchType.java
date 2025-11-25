package app.model;

public enum ResearchType {
    // Military - Infantry & Defense
    PHALANX_FORMATION,      // +20% defense Spears/Guards
    ADVANCED_METALLURGY,    // +15% attack Melee units
    SHIELD_WALL,            // +10% defense vs Ranged (Immortals)
    SIEGE_TACTICS,          // Bonus damage vs Walls (War Barges/Infantry)

    // Military - Ranged
    COMPOSITE_BOW,          // +15% range/attack Archers
    HAIL_OF_ARROWS,         // Special: Area damage (Master Archers)

    // Military - Cavalry & Chariots
    EQUESTRIAN_ART,         // +10% speed Cavalry/Scouts
    CHARIOT_MASTERY,        // +15% attack Chariots
    SCYTHED_WHEELS,         // Bonus damage vs Infantry (Scythe Chariots)

    // Military - Naval
    BITUMEN_FIRE,           // Bonus attack for War Barges

    // Civil
    ADVANCED_IRRIGATION,    // +20% Wheat production
    ARCHITECTURE,           // -15% construction cost
    RIVER_NAVIGATION,       // +25% fleet speed

    // Religious
    SACRED_RITUALS,         // Amplified Divine Tears effects
    DIVINE_BLESSINGS,       // Temporary army bonuses
    MYTHICAL_BINDING,       // Reduces cost of Myth units

    // Commercial
    TRADE_ROUTES,           // -20% transport cost
    NEGOTIATION,            // +10% trade profits
    CURRENCY                // Advanced market system
}
