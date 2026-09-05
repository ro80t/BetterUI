package com.ro80t.betterui.compat.fabric.commonlegacy;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Draws the player's coordinates, rounded to one decimal place, as a small
 * line in the top-left corner of the screen, below the FPS display. Toggle
 * with {@code coordinatesDisplayEnabled} in the mod's config file.
 * <p>
 * Shared by the pre-DrawContext Fabric versions (1182/1192). 1165 predates
 * those too and keeps its own standalone copy.
 */
public final class CoordinatesOverlay {
    private static final int MARGIN = 2;
    private static final int ROW_Y = MARGIN + 10;

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
        client.textRenderer.drawWithShadow(matrices, text, MARGIN, ROW_Y, 0xFFFFFFFF);
    }
}
