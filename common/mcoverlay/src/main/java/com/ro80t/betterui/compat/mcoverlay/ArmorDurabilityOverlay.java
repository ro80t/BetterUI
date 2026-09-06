package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability, at a position/scale editable via the BetterUI
 * position editor screen ({@code armorHudLayout} in the config), stacked
 * either vertically or horizontally depending on {@code armorHudVertical}.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * Vanilla-only (no {@code net.minecraftforge}/{@code net.neoforged} types),
 * so every Forge and NeoForge version except 1218 embeds this same compiled
 * class and only supplies the loader-specific event subscription that calls
 * {@link #render(GuiGraphics)}. 1218 uses {@code common:mcoverlay-v1218}'s
 * own copy instead, because {@code GuiGraphics.drawString(...)} changed its
 * return type from {@code int} to {@code void} in that version - a class
 * compiled against this module's 1.20.1 mappings would throw
 * {@code NoSuchMethodError} when run against 1.21.8's real class.
 */
public final class ArmorDurabilityOverlay {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    static final int MAX_ROWS = ARMOR_SLOTS_BOTTOM_UP.length;
    static final int ICON_SIZE = 16;
    static final int ROW_HEIGHT = 18;
    private static final int COLUMN_GAP = 4;
    private static final String WIDTH_SAMPLE_TEXT = "9999/9999";

    private ArmorDurabilityOverlay() {
    }

    /**
     * Width reserved for one column in horizontal mode: wide enough for the
     * icon plus a generously-sized durability string, so columns never
     * overlap regardless of how long the actual "remaining/max" text is.
     * Fixed (not fitted to the real text) so the BetterUI position editor's
     * preview can reserve the exact same width and always match reality -
     * see {@link BetterUiPositionEditorScreen#drawArmorHudPreview}.
     */
    static int columnWidth(final Font font) {
        return ICON_SIZE + COLUMN_GAP + font.width(WIDTH_SAMPLE_TEXT) + COLUMN_GAP;
    }

    public static void render(final GuiGraphics context) {
        if (!BetterUiMod.getConfig().isDurabilityHudEnabled()) {
            return;
        }

        final Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }
        final LocalPlayer player = client.player;
        if (player == null || client.screen != null) {
            return;
        }

        final HudLayout layout = BetterUiMod.getConfig().getArmorHudLayout();
        final boolean vertical = BetterUiMod.getConfig().isArmorHudVertical();

        final int columnWidth = columnWidth(client.font);

        context.pose().pushPose();
        context.pose().translate(layout.x(context.guiWidth()), layout.y(context.guiHeight()), 0.0F);
        context.pose().scale(layout.getScale(), layout.getScale(), 1.0F);

        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final DurabilityEntry entry = new DurabilityEntry(player.getItemBySlot(slot));
            if (!entry.isVisible()) {
                continue;
            }

            entry.draw(context, client.font, iconPosition(row, vertical, columnWidth));
            row++;
        }

        context.pose().popPose();
    }

    static Position iconPosition(final int row, final boolean vertical, final int columnWidth) {
        return vertical ? new Position(0, -row * ROW_HEIGHT) : new Position(-row * columnWidth, 0);
    }

    record Position(int x, int y) {
    }

    private record DurabilityEntry(ItemStack stack) {
        boolean isVisible() {
            return !stack.isEmpty() && stack.isDamageableItem();
        }

        private int remaining() {
            return stack.getMaxDamage() - stack.getDamageValue();
        }

        private int color() {
            return stack.getItem().getBarColor(stack) | 0xFF000000;
        }

        private String text() {
            return remaining() + "/" + stack.getMaxDamage();
        }

        void draw(final GuiGraphics context, final Font font, final Position iconPos) {
            final int textX = iconPos.x() - 4 - font.width(text());
            final int textY = iconPos.y() + (ICON_SIZE - font.lineHeight) / 2;

            context.renderItem(stack, iconPos.x(), iconPos.y());
            context.drawString(font, text(), textX, textY, color());
        }
    }
}
