package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Draws the current FPS as a small number in the top-left corner of the
 * screen. Toggle with {@code fpsDisplayEnabled} in the mod's config file.
 * <p>
 * Byte-identical to {@code fabric:common-drawcontext}'s copy, but compiled
 * separately against 1218 mappings because {@code DrawContext.drawTextWithShadow(...)}
 * changed its return type from {@code int} to {@code void} in that version -
 * see {@link ArmorDurabilityOverlay} for why. Used only by the 1218 Fabric
 * module.
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
