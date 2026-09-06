package com.ro80t.betterui.compat.fabric.v1165;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Draws the player's coordinates, rounded to one decimal place, at a
 * position and scale editable via the BetterUI position editor screen
 * ({@code coordinatesLayout} in the config).
 */
public final class CoordinatesOverlay {
    private CoordinatesOverlay() {
    }

    public static void render(final MatrixStack matrices) {
        if (!BetterUiMod.getConfig().isCoordinatesDisplayEnabled()) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) {
            return;
        }

        final ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        final String text = String.format(
                "X: %.1f Y: %.1f Z: %.1f", player.getX(), player.getY(), player.getZ());

        final HudLayout layout = BetterUiMod.getConfig().getCoordinatesLayout();
        final int screenWidth = client.getWindow().getScaledWidth();
        final int screenHeight = client.getWindow().getScaledHeight();

        matrices.push();
        matrices.translate(layout.x(screenWidth), layout.y(screenHeight), 0.0F);
        matrices.scale(layout.getScale(), layout.getScale(), 1.0F);
        client.textRenderer.drawWithShadow(matrices, text, 0, 0, 0xFFFFFFFF);
        matrices.pop();
    }
}
