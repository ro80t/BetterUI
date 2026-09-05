package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Draws the current FPS as a small number in the top-left corner of the
 * screen. Toggle with {@code fpsDisplayEnabled} in the mod's config file.
 * <p>
 * Vanilla-only, shared unchanged by every Forge and NeoForge version except
 * 1218, which uses {@code common:mcoverlay-v1218}'s own copy - see
 * {@link ArmorDurabilityOverlay} for why.
 */
public final class FpsOverlay {
    private static final int MARGIN = 2;

    private FpsOverlay() {
    }

    public static void render(final GuiGraphics context) {
        if (!BetterUiMod.getConfig().isFpsDisplayEnabled()) {
            return;
        }

        final Minecraft client = Minecraft.getInstance();
        if (client == null || client.screen != null) {
            return;
        }

        context.drawString(client.font, client.getFps() + " FPS", MARGIN, MARGIN, 0xFFFFFFFF);
    }
}
