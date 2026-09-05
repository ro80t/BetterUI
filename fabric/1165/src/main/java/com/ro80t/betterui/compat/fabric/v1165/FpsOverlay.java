package com.ro80t.betterui.compat.fabric.v1165;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.fabric.v1165.mixin.minecraft.MixinMinecraftClientFpsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Draws the current FPS as a small number in the top-left corner of the
 * screen. Toggle with {@code fpsDisplayEnabled} in the mod's config file.
 */
public final class FpsOverlay {
    private static final int MARGIN = 2;

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

        final String text = MixinMinecraftClientFpsAccessor.betterui$getCurrentFps() + " FPS";
        client.textRenderer.drawWithShadow(matrices, text, MARGIN, MARGIN, 0xFFFFFFFF);
    }
}
