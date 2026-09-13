package net.plunzi.punchout.cards;

import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The four displays that make up one rendered card slot: the card itself, the icon on
 * top of it, the pointer above it and the description text below it.
 *
 * <p>Instances are created by {@link CardRenderer#render(org.bukkit.World, List)} and stay
 * valid until {@link #despawn()} (or {@link CardRenderer#despawn()}) is called.
 */
public final class CardSlotDisplays {

    private final int slotIndex;
    private final CardRenderConfig.SlotSpec slot;
    private final Card card;
    private final ItemDisplay cardDisplay;
    private final ItemDisplay iconDisplay;
    private final ItemDisplay pointerDisplay;
    private final TextDisplay textDisplay;
    private final ItemStack cardItem;
    private final ItemStack selectedCardItem;
    private final ItemStack pointerItem;

    CardSlotDisplays(
            int slotIndex,
            CardRenderConfig.SlotSpec slot,
            Card card,
            ItemDisplay cardDisplay,
            ItemDisplay iconDisplay,
            ItemDisplay pointerDisplay,
            TextDisplay textDisplay,
            ItemStack cardItem,
            ItemStack selectedCardItem,
            ItemStack pointerItem) {
        this.slotIndex = slotIndex;
        this.slot = slot;
        this.card = card;
        this.cardDisplay = cardDisplay;
        this.iconDisplay = iconDisplay;
        this.pointerDisplay = pointerDisplay;
        this.textDisplay = textDisplay;
        this.cardItem = cardItem;
        this.selectedCardItem = selectedCardItem;
        this.pointerItem = pointerItem;
    }

    /** Zero-based slot index, matching the order of {@code Slots} in the config. */
    public int slotIndex() {
        return slotIndex;
    }

    public CardRenderConfig.SlotSpec slot() {
        return slot;
    }

    public Card card() {
        return card;
    }

    public ItemDisplay cardDisplay() {
        return cardDisplay;
    }

    public ItemDisplay iconDisplay() {
        return iconDisplay;
    }

    public ItemDisplay pointerDisplay() {
        return pointerDisplay;
    }

    public TextDisplay textDisplay() {
        return textDisplay;
    }

    public List<Display> displays() {
        return List.of(cardDisplay, iconDisplay, pointerDisplay, textDisplay);
    }

    /** Shows or hides the pointer above this slot without respawning anything. */
    public void setPointerVisible(boolean visible) {
        pointerDisplay.setItemStack(visible ? pointerItem.clone() : null);
    }

    /**
     * Swaps the card model between its rarity model and the {@code Selector} model from
     * the config.
     */
    public void setSelected(boolean selected) {
        cardDisplay.setItemStack((selected ? selectedCardItem : cardItem).clone());
    }

    /** Removes all four displays. Safe to call more than once. */
    public void despawn() {
        displays().forEach(Display::remove);
    }
}
