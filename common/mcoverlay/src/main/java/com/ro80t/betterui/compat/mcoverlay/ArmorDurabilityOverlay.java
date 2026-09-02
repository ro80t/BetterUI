package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability in the bottom-right corner of the screen.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * Vanilla-only (no {@code net.minecraftforge}/{@code net.neoforged} types),
 * so every Forge and NeoForge version embeds this same compiled class and
 * only supplies the loader-specific event subscription that calls
 * {@link #render(GuiGraphics)}.
 */
public final class ArmorDurabilityOverlay {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    private ArmorDurabilityOverlay() {
    }

    public static void render(final GuiGraphics context) {
        if (!BetterUiMod.getConfig().isDurabilityHudEnabled()) {
            return;
        }

        final Minecraft client = Minecraft.getInstance();
        final LocalPlayer player = client.player;
        if (player == null || client.screen != null) {
            return;
        }

        final RowLayout layout = new RowLayout(context.guiWidth(), context.guiHeight());
        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final DurabilityEntry entry = new DurabilityEntry(player.getItemBySlot(slot));
            if (!entry.isVisible()) {
                continue;
            }

            entry.draw(context, client.font, layout.iconPosition(row));
            row++;
        }
    }

    private record Position(int x, int y) {
    }

    private record RowLayout(int screenWidth, int screenHeight) {
        private static final int MARGIN = 6;
        private static final int ROW_HEIGHT = 18;

        Position iconPosition(final int row) {
            return new Position(
                    screenWidth - MARGIN - DurabilityEntry.ICON_SIZE,
                    screenHeight - MARGIN - DurabilityEntry.ICON_SIZE - row * ROW_HEIGHT
            );
        }
    }

    private record DurabilityEntry(ItemStack stack) {
        private static final int ICON_SIZE = 16;

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
