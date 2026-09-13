package net.plunzi.punchout.cards;

import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

/**
 * One card offered to the player. This is the input to
 * {@link CardRenderer#render(org.bukkit.World, List)}; the renderer turns it into a card,
 * an icon, a pointer and a text display.
 *
 * @param rarity       picks the card and pointer models from the config
 * @param iconMaterial base item of the icon display, normally {@link #DEFAULT_ICON_MATERIAL}
 * @param iconModel    {@code custom_model_data} string of the icon, e.g. {@code plunzish:item/logo}
 * @param description  MiniMessage lines shown below the card, one list entry per line
 */
public record Card(Rarity rarity, Material iconMaterial, String iconModel, List<String> description) {

    /** Item every display in the card row is built from. */
    public static final Material DEFAULT_ICON_MATERIAL = Material.HEART_OF_THE_SEA;

    public Card {
        Objects.requireNonNull(rarity, "rarity");
        Objects.requireNonNull(iconMaterial, "iconMaterial");
        Objects.requireNonNull(iconModel, "iconModel");
        description = List.copyOf(Objects.requireNonNull(description, "description"));
    }

    /** Card using {@link #DEFAULT_ICON_MATERIAL} as the icon item. */
    public Card(Rarity rarity, String iconModel, List<String> description) {
        this(rarity, DEFAULT_ICON_MATERIAL, iconModel, description);
    }

    /** Card using {@link #DEFAULT_ICON_MATERIAL} and a varargs description. */
    public Card(Rarity rarity, String iconModel, String... description) {
        this(rarity, DEFAULT_ICON_MATERIAL, iconModel, List.of(description));
    }
}
