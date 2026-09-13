package net.plunzi.punchout.cards;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable, parsed form of {@code cards-render-config.yml}.
 *
 * <p>Load it once in {@code onEnable} with {@link #load(Plugin)} and hand it to a
 * {@link CardRenderer}.
 *
 * @param rarities   card and pointer model ids per rarity
 * @param selectorModel model id of the "card is selected" variant
 * @param spawn      where players are put for the card screen
 * @param button     confirm-button location
 * @param card       card display scale and world height
 * @param icon       icon display scale and world height
 * @param text       description display scale and world height
 * @param pointer    pointer display scale and world height
 * @param slots      the five slot positions, in file order
 * @param baseYaw    added to every slot rotation; rotates the whole fan at once
 */
public record CardRenderConfig(
        Map<Rarity, RarityModels> rarities,
        String selectorModel,
        PointSpec spawn,
        PointSpec button,
        DisplaySpec card,
        DisplaySpec icon,
        DisplaySpec text,
        DisplaySpec pointer,
        List<SlotSpec> slots,
        float baseYaw
) {

    /** File name inside the plugin data folder. */
    public static final String FILE_NAME = "cards-render-config.yml";

    /** Alternative spelling accepted if it is the one present on disk. */
    public static final String ALTERNATE_FILE_NAME = "cards-render-config.yaml";

    public CardRenderConfig {
        rarities = Map.copyOf(rarities);
        slots = List.copyOf(slots);
    }

    /** Card and pointer {@code custom_model_data} strings of one rarity. */
    public record RarityModels(String card, String pointer) { }

    /**
     * Scale and world height of one display kind. X/Z come from the slot, Y from here.
     */
    public record DisplaySpec(float scaleX, float scaleY, float scaleZ, double height) {

        /** Fresh transformation carrying only this spec's scale. */
        public Transformation transformation() {
            return new Transformation(
                    new Vector3f(),
                    new AxisAngle4f(),
                    new Vector3f(scaleX, scaleY, scaleZ),
                    new AxisAngle4f());
        }
    }

    /** A fixed point with a facing, used for the player spawn and the button. */
    public record PointSpec(double x, double y, double z, float yaw, float pitch) {

        public Location toLocation(World world) {
            return new Location(world, x, y, z, yaw, pitch);
        }
    }

    /**
     * Horizontal position and rotation of one card slot. All four displays of a slot
     * share these; only their Y and scale differ.
     *
     * @param name     config key, e.g. {@code Slot 1}
     * @param x        world X
     * @param z        world Z
     * @param rotation yaw of the slot's displays, in degrees
     */
    public record SlotSpec(String name, double x, double z, float rotation) { }

    public RarityModels models(Rarity rarity) {
        RarityModels models = rarities.get(rarity);
        if (models == null) {
            throw new CardRenderConfigException("no model ids configured for rarity " + rarity);
        }
        return models;
    }

    public int slotCount() {
        return slots.size();
    }

    /**
     * Loads the config from the plugin data folder, writing the bundled default first
     * if neither spelling of the file exists yet.
     */
    public static CardRenderConfig load(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.isFile()) {
            File alternate = new File(plugin.getDataFolder(), ALTERNATE_FILE_NAME);
            if (alternate.isFile()) {
                file = alternate;
            } else {
                plugin.saveResource(FILE_NAME, false);
            }
        }
        return load(YamlConfiguration.loadConfiguration(file));
    }

    /** Parses an already loaded YAML document. */
    public static CardRenderConfig load(ConfigurationSection yaml) {
        YamlNode root = YamlNode.of("", yaml);

        YamlNode entities = root.child("Enteties");
        if (entities.isEmpty()) {
            // Tolerate the corrected spelling too.
            entities = root.child("Entities");
        }
        if (entities.isEmpty()) {
            throw new CardRenderConfigException("missing required section 'Enteties'");
        }

        return new CardRenderConfig(
                readRarities(root.child("Rarities")),
                root.child("Selector").string("model_id"),
                readPoint(entities.child("Spawn").child("Location"), true),
                readPoint(entities.child("Button").child("Location"), false),
                readDisplay(entities, "Card"),
                readDisplay(entities, "Icon"),
                readDisplay(entities, "Text"),
                readDisplay(entities, "Pointer"),
                readSlots(root.child("Slots")),
                (float) entities.number("base_yaw", 0.0D));
    }

    private static Map<Rarity, RarityModels> readRarities(YamlNode node) {
        Map<Rarity, RarityModels> rarities = new EnumMap<>(Rarity.class);
        for (String key : node.keys()) {
            Rarity rarity;
            try {
                rarity = Rarity.byName(key);
            } catch (IllegalArgumentException exception) {
                throw new CardRenderConfigException("Rarities." + key + ": " + exception.getMessage(), exception);
            }
            YamlNode models = node.child(key).child("model_ids");
            rarities.put(rarity, new RarityModels(models.string("card"), models.string("pointer")));
        }

        List<Rarity> missing = new ArrayList<>();
        for (Rarity rarity : Rarity.values()) {
            if (!rarities.containsKey(rarity)) {
                missing.add(rarity);
            }
        }
        if (!missing.isEmpty()) {
            throw new CardRenderConfigException("Rarities is missing entries for " + missing);
        }
        return rarities;
    }

    private static DisplaySpec readDisplay(YamlNode entities, String name) {
        YamlNode node = entities.child(name);
        if (node.isEmpty()) {
            throw new CardRenderConfigException("missing required section 'Enteties." + name + "'");
        }
        YamlNode scale = node.child("Scale");
        return new DisplaySpec(
                (float) scale.number("x"),
                (float) scale.number("y"),
                (float) scale.number("z"),
                node.child("Height").number("y"));
    }

    private static PointSpec readPoint(YamlNode node, boolean withFacing) {
        return new PointSpec(
                node.number("x"),
                node.number("y", 0.0D),
                node.number("z"),
                withFacing ? (float) node.number("rotation", 0.0D) : 0.0F,
                withFacing ? (float) node.number("facing", 0.0D) : 0.0F);
    }

    private static List<SlotSpec> readSlots(YamlNode node) {
        List<SlotSpec> slots = new ArrayList<>();
        for (String key : node.keys()) {
            YamlNode slot = node.child(key);
            YamlNode location = slot.child("Location");
            slots.add(new SlotSpec(
                    key,
                    location.number("x"),
                    location.number("z"),
                    (float) slot.number("rotation", 0.0D)));
        }
        if (slots.isEmpty()) {
            throw new CardRenderConfigException("Slots must define at least one slot");
        }
        return slots;
    }
}
