package com.ro80t.betterui.compat.fabric.v1201;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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

    public static void render(final DrawContext context) {
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
        final MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(layout.x(context.getScaledWindowWidth()), layout.y(context.getScaledWindowHeight()), 0.0F);
        matrices.scale(layout.getScale(), layout.getScale(), 1.0F);
        context.drawTextWithShadow(client.textRenderer, text, 0, 0, 0xFFFFFFFF);
        matrices.pop();
    }
}
