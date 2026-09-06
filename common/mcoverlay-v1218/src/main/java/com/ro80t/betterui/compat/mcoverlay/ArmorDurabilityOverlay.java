package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability, at a position/scale editable via the BetterUI
 * position editor screen ({@code armorHudLayout} in the config), stacked
 * either vertically or horizontally depending on {@code armorHudVertical}.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * Vanilla-only (no {@code net.minecraftforge}/{@code net.neoforged} types).
 * Byte-identical in intent to {@code common:mcoverlay}'s copy, but compiled
 * separately against 1.21.8 mappings and using {@link Matrix3x2fStack}
 * instead of {@code PoseStack}, because {@code GuiGraphics.drawString(...)}
 * changed its return type from {@code int} to {@code void} and
 * {@code GuiGraphics.pose()} changed type in that version - a class compiled
 * against this module's 1.20.1 mappings would throw {@code NoSuchMethodError}
 * when run against 1.21.8's real class. Used only by the 1218 Forge/NeoForge
 * modules.
 */
public final class ArmorDurabilityOverlay {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    private static final int ROW_HEIGHT = 18;
    private static final int ROW_WIDTH = 56;

    private ArmorDurabilityOverlay() {
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

        final Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        matrices.translate(layout.x(context.guiWidth()), layout.y(context.guiHeight()));
        matrices.scale(layout.getScale());

        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final DurabilityEntry entry = new DurabilityEntry(player.getItemBySlot(slot));
            if (!entry.isVisible()) {
                continue;
            }

            entry.draw(context, client.font, iconPosition(row, vertical));
            row++;
        }

        matrices.popMatrix();
    }

    private static Position iconPosition(final int row, final boolean vertical) {
        return vertical ? new Position(0, -row * ROW_HEIGHT) : new Position(-row * ROW_WIDTH, 0);
    }

    private record Position(int x, int y) {
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
