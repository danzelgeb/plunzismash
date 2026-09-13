package net.plunzi.punchout.cards;

import java.util.Locale;

/**
 * Card rarity enumeration.
 */
public enum Rarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY;

    public static Rarity byName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Rarity name cannot be null");
        }
        for (Rarity rarity : values()) {
            if (rarity.name().equalsIgnoreCase(name)) {
                return rarity;
            }
        }
        throw new IllegalArgumentException("Unknown rarity: " + name);
    }
}
