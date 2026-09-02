package com.ro80t.betterui.compat.fabric.commonlegacy;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability in the bottom-right corner of the screen.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * Shared by the pre-DrawContext Fabric versions that still expose
 * {@code Item.getItemBarColor}/{@code isItemBarVisible} (1182/1192). 1165
 * predates those too and keeps its own standalone copy.
 */
public final class ArmorDurabilityOverlay {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    private ArmorDurabilityOverlay() {
    }

    public static void render(final MatrixStack matrices) {
        if (!BetterUiMod.getConfig().isDurabilityHudEnabled()) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        final PlayerEntity player = client.player;
        if (player == null || client.currentScreen != null) {
            return;
        }

        final RowLayout layout = new RowLayout(
                client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final DurabilityEntry entry = new DurabilityEntry(player.getEquippedStack(slot));
            if (!entry.isVisible()) {
                continue;
            }

            entry.draw(matrices, client.textRenderer, client.getItemRenderer(), layout.iconPosition(row));
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
            return !stack.isEmpty() && stack.isDamageable();
        }

        private int remaining() {
            return stack.getMaxDamage() - stack.getDamage();
        }

        private int color() {
            return stack.getItem().getItemBarColor(stack) | 0xFF000000;
        }

        private String text() {
            return remaining() + "/" + stack.getMaxDamage();
        }

        void draw(final MatrixStack matrices, final TextRenderer textRenderer,
                  final net.minecraft.client.render.item.ItemRenderer itemRenderer, final Position iconPos) {
            final int textX = iconPos.x() - 4 - textRenderer.getWidth(text());
            final int textY = iconPos.y() + (ICON_SIZE - textRenderer.fontHeight) / 2;

            itemRenderer.renderGuiItemIcon(stack, iconPos.x(), iconPos.y());
            textRenderer.drawWithShadow(matrices, text(), textX, textY, color());
        }
    }
}
