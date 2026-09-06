package com.ro80t.betterui.compat.fabric.v1165;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.fabric.v1165.mixin.minecraft.MixinMinecraftClientFpsAccessor;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Draws the current FPS as a small number, at a position and scale editable
 * via the BetterUI position editor screen ({@code fpsLayout} in the config).
 */
public final class FpsOverlay {
    private FpsOverlay() {
    }

    public static void render(final MatrixStack matrices) {
        if (!BetterUiMod.getConfig().isFpsDisplayEnabled()) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) {
            return;
        }

        final HudLayout layout = BetterUiMod.getConfig().getFpsLayout();
        final int screenWidth = client.getWindow().getScaledWidth();
        final int screenHeight = client.getWindow().getScaledHeight();

        matrices.push();
        matrices.translate(layout.x(screenWidth), layout.y(screenHeight), 0.0F);
        matrices.scale(layout.getScale(), layout.getScale(), 1.0F);
        client.textRenderer.drawWithShadow(matrices, MixinMinecraftClientFpsAccessor.betterui$getCurrentFps() + " FPS", 0, 0, 0xFFFFFFFF);
        matrices.pop();
    }
}
