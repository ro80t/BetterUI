package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability in the bottom-right corner of the screen.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * Byte-identical to {@code fabric:common-drawcontext}'s copy, but compiled
 * separately against 1218 mappings because {@code DrawContext.drawTextWithShadow(...)}
 * changed its return type from {@code int} to {@code void} in that version,
 * which is a binary-incompatible change: a class compiled against the older
 * signature throws {@code NoSuchMethodError} when run against the newer one,
 * and vice versa. Used only by the 1218 Fabric module.
 */
public final class ArmorDurabilityOverlay {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    private ArmorDurabilityOverlay() {
    }

    public static void render(final DrawContext context) {
        if (!BetterUiMod.getConfig().isDurabilityHudEnabled()) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        final PlayerEntity player = client.player;
        if (player == null || client.currentScreen != null) {
            return;
        }

        final RowLayout layout = new RowLayout(context.getScaledWindowWidth(), context.getScaledWindowHeight());
        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final DurabilityEntry entry = new DurabilityEntry(player.getEquippedStack(slot));
            if (!entry.isVisible()) {
                continue;
            }

            entry.draw(context, client.textRenderer, layout.iconPosition(row));
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

        void draw(final DrawContext context, final TextRenderer textRenderer, final Position iconPos) {
            final int textX = iconPos.x() - 4 - textRenderer.getWidth(text());
            final int textY = iconPos.y() + (ICON_SIZE - textRenderer.fontHeight) / 2;

            context.drawItem(stack, iconPos.x(), iconPos.y());
            context.drawTextWithShadow(textRenderer, text(), textX, textY, color());
        }
    }
}
