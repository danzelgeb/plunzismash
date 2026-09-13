package net.plunzi.punchout.cards;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Spawns and removes the card row described by {@code cards-render-config.yml}.
 *
 * <p>Each card in the list passed to {@link #render(World, List)} is drawn into the next
 * configured slot as four display entities:
 * <ul>
 *   <li>the card, an item display using the rarity's {@code card} model,</li>
 *   <li>the icon, an item display using the card's own model,</li>
 *   <li>the pointer, an item display using the rarity's {@code pointer} model,</li>
 *   <li>the description, a text display holding the MiniMessage lines.</li>
 * </ul>
 * All four share the slot's X/Z and yaw and differ only in height and scale.
 *
 * <p>Must be used from the main server thread, like all entity spawning.
 */
public final class CardRenderer {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /** Item the card, pointer and (by default) icon displays are built from. */
    private static final Material DISPLAY_MATERIAL = Card.DEFAULT_ICON_MATERIAL;

    /** Wide enough that only our own newlines wrap the description. */
    private static final int TEXT_LINE_WIDTH = 1000;

    private final CardRenderConfig config;
    private final List<CardSlotDisplays> rendered = new ArrayList<>();

    public CardRenderer(CardRenderConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    public CardRenderConfig config() {
        return config;
    }

    /** The slots currently on screen, empty when nothing is rendered. */
    public List<CardSlotDisplays> rendered() {
        return Collections.unmodifiableList(rendered);
    }

    public boolean isRendered() {
        return !rendered.isEmpty();
    }

    /**
     * Removes anything previously rendered and draws {@code cards} into the configured
     * slots, in order.
     *
     * @param world world to spawn the displays in
     * @param cards cards to draw; normally five, one per slot
     * @return a handle per rendered slot, in slot order
     * @throws IllegalArgumentException if there are more cards than configured slots
     */
    public List<CardSlotDisplays> render(World world, List<Card> cards) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(cards, "cards");
        if (cards.size() > config.slotCount()) {
            throw new IllegalArgumentException(
                    "got " + cards.size() + " cards but only " + config.slotCount() + " slots are configured");
        }

        despawn();
        for (int index = 0; index < cards.size(); index++) {
            rendered.add(spawnSlot(world, index, cards.get(index)));
        }
        return rendered();
    }

    /** Removes every display this renderer spawned. */
    public void despawn() {
        rendered.forEach(CardSlotDisplays::despawn);
        rendered.clear();
    }

    /** Shows the pointer on exactly one slot and hides it on all others. */
    public void setPointer(int slotIndex) {
        for (CardSlotDisplays slot : rendered) {
            slot.setPointerVisible(slot.slotIndex() == slotIndex);
        }
    }

    private CardSlotDisplays spawnSlot(World world, int index, Card card) {
        CardRenderConfig.SlotSpec slot = config.slots().get(index);
        CardRenderConfig.RarityModels models = config.models(card.rarity());
        float yaw = slot.rotation() + config.baseYaw();

        ItemStack cardItem = modelledItem(DISPLAY_MATERIAL, models.card());
        ItemStack selectedCardItem = modelledItem(DISPLAY_MATERIAL, config.selectorModel());
        ItemStack pointerItem = modelledItem(DISPLAY_MATERIAL, models.pointer());
        ItemStack iconItem = modelledItem(card.iconMaterial(), card.iconModel());

        ItemDisplay cardDisplay = spawnItemDisplay(world, slot, config.card(), yaw, cardItem);
        ItemDisplay iconDisplay = spawnItemDisplay(world, slot, config.icon(), yaw, iconItem);
        ItemDisplay pointerDisplay = spawnItemDisplay(world, slot, config.pointer(), yaw, pointerItem);
        TextDisplay textDisplay = spawnTextDisplay(world, slot, config.text(), yaw, card.description());

        return new CardSlotDisplays(
                index, slot, card,
                cardDisplay, iconDisplay, pointerDisplay, textDisplay,
                cardItem, selectedCardItem, pointerItem);
    }

    private ItemDisplay spawnItemDisplay(
            World world,
            CardRenderConfig.SlotSpec slot,
            CardRenderConfig.DisplaySpec spec,
            float yaw,
            ItemStack item) {
        return world.spawn(locationOf(world, slot, spec, yaw), ItemDisplay.class, display -> {
            applyCommon(display, spec);
            display.setItemStack(item.clone());
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        });
    }

    private TextDisplay spawnTextDisplay(
            World world,
            CardRenderConfig.SlotSpec slot,
            CardRenderConfig.DisplaySpec spec,
            float yaw,
            List<String> description) {
        return world.spawn(locationOf(world, slot, spec, yaw), TextDisplay.class, display -> {
            applyCommon(display, spec);
            display.text(deserialize(description));
            display.setAlignment(TextDisplay.TextAlignment.CENTER);
            display.setLineWidth(TEXT_LINE_WIDTH);
            display.setShadowed(false);
            display.setSeeThrough(false);
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        });
    }

    private void applyCommon(Display display, CardRenderConfig.DisplaySpec spec) {
        display.setTransformation(spec.transformation());
        display.setBillboard(Display.Billboard.FIXED);
        display.setInterpolationDuration(0);
        display.setInterpolationDelay(0);
        // UI entities: never write them to the region files.
        display.setPersistent(false);
    }

    /**
     * The world position of one display: X/Z and yaw from the slot, Y from the display
     * kind. Offset the whole card row here if the arena ever moves.
     */
    private Location locationOf(
            World world,
            CardRenderConfig.SlotSpec slot,
            CardRenderConfig.DisplaySpec spec,
            float yaw) {
        return new Location(world, slot.x(), spec.height(), slot.z(), yaw, 0.0F);
    }

    /** Joins the MiniMessage lines with newlines, one list entry per line. */
    public static Component deserialize(List<String> lines) {
        return Component.join(
                JoinConfiguration.newlines(),
                lines.stream().map(MINI_MESSAGE::deserialize).toList());
    }

    /** An item carrying a single {@code custom_model_data} string. */
    public static ItemStack modelledItem(Material material, String modelId) {
        ItemStack item = ItemStack.of(material);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(modelId));
        return item;
    }

    /** Convenience for callers that do not track a world themselves. */
    public World defaultWorld() {
        List<World> worlds = Bukkit.getWorlds();
        if (worlds.isEmpty()) {
            throw new IllegalStateException("no worlds are loaded");
        }
        return worlds.getFirst();
    }
}
