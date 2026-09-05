package com.ro80t.betterui.compat.fabric.commonlegacy;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.fabric.commonlegacy.mixin.minecraft.MixinMinecraftClientFpsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Draws the current FPS as a small number in the top-left corner of the
 * screen. Toggle with {@code fpsDisplayEnabled} in the mod's config file.
 * <p>
 * Shared by the pre-DrawContext Fabric versions that still expose
 * {@code Item.getItemBarColor}/{@code isItemBarVisible} (1182/1192). 1165
 * predates those too and keeps its own standalone copy.
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
