package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Draws the current FPS as a small number in the top-left corner of the
 * screen. Toggle with {@code fpsDisplayEnabled} in the mod's config file.
 * <p>
 * Shared by every Fabric version whose {@code DrawContext} still exposes
 * {@code drawTextWithShadow(...)} returning {@code int} (1211/1214). 1218
 * changed that to {@code void} and keeps its own copy in
 * {@code fabric:common-drawcontext-v1218} - see
 * {@link ArmorDurabilityOverlay} for why.
 */
public final class FpsOverlay {
    private static final int MARGIN = 2;

    private FpsOverlay() {
    }

    public static void render(final DrawContext context) {
        if (!BetterUiMod.getConfig().isFpsDisplayEnabled()) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) {
            return;
        }

        context.drawTextWithShadow(client.textRenderer, client.getCurrentFps() + " FPS", MARGIN, MARGIN, 0xFFFFFFFF);
    }
}
