package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

/**
 * Draws the player's coordinates, rounded to one decimal place, as a small
 * line in the top-left corner of the screen, below the FPS display. Toggle
 * with {@code coordinatesDisplayEnabled} in the mod's config file.
 * <p>
 * Vanilla-only, shared unchanged by every Forge and NeoForge version except
 * 1218, which uses {@code common:mcoverlay-v1218}'s own copy - see
 * {@link ArmorDurabilityOverlay} for why.
 */
public final class CoordinatesOverlay {
    private static final int MARGIN = 2;
    private static final int ROW_Y = MARGIN + 10;

    private CoordinatesOverlay() {
    }

    public static void render(final GuiGraphics context) {
        if (!BetterUiMod.getConfig().isCoordinatesDisplayEnabled()) {
            return;
        }

        final Minecraft client = Minecraft.getInstance();
        if (client == null || client.screen != null) {
            return;
        }

        final LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        final String text = String.format(
                "X: %.1f Y: %.1f Z: %.1f", player.getX(), player.getY(), player.getZ());
        context.drawString(client.font, text, MARGIN, ROW_Y, 0xFFFFFFFF);
    }
}
