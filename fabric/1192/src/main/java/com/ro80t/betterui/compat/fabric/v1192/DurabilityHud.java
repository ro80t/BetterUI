package com.ro80t.betterui.compat.fabric.v1192;

import com.ro80t.betterui.BetterUiMod;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability in the bottom-right corner of the screen.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 */
public final class DurabilityHud {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    private static final int MARGIN = 6;
    private static final int ROW_HEIGHT = 18;
    private static final int ICON_SIZE = 16;

    private DurabilityHud() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register(DurabilityHud::render);
    }

    private static void render(final MatrixStack matrices, final float tickDelta) {
        if (!BetterUiMod.getConfig().isDurabilityHudEnabled()) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        final PlayerEntity player = client.player;
        if (player == null || client.currentScreen != null) {
            return;
        }

        final TextRenderer textRenderer = client.textRenderer;
        final int screenWidth = client.getWindow().getScaledWidth();
        final int screenHeight = client.getWindow().getScaledHeight();

        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty() || !stack.isDamageable()) {
                continue;
            }

            final Item item = stack.getItem();
            final int remaining = stack.getMaxDamage() - stack.getDamage();
            final int color = item.getItemBarColor(stack) | 0xFF000000;
            final String text = remaining + "/" + stack.getMaxDamage();

            final int iconX = screenWidth - MARGIN - ICON_SIZE;
            final int iconY = screenHeight - MARGIN - ICON_SIZE - row * ROW_HEIGHT;
            final int textX = iconX - 4 - textRenderer.getWidth(text);
            final int textY = iconY + (ICON_SIZE - textRenderer.fontHeight) / 2;

            client.getItemRenderer().renderGuiItemIcon(stack, iconX, iconY);
            textRenderer.drawWithShadow(matrices, text, textX, textY, color);

            row++;
        }
    }
}
