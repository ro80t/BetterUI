package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

/**
 * Draws the player's coordinates, rounded to one decimal place, at a
 * position and scale editable via the BetterUI position editor screen
 * ({@code coordinatesLayout} in the config).
 * <p>
 * Vanilla-only, shared unchanged by every Forge and NeoForge version except
 * 1218, which uses {@code common:mcoverlay-v1218}'s own copy - see
 * {@link ArmorDurabilityOverlay} for why.
 */
public final class CoordinatesOverlay {
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

        final HudLayout layout = BetterUiMod.getConfig().getCoordinatesLayout();
        context.pose().pushPose();
        context.pose().translate(layout.x(context.guiWidth()), layout.y(context.guiHeight()), 0.0F);
        context.pose().scale(layout.getScale(), layout.getScale(), 1.0F);
        context.drawString(client.font, text, 0, 0, 0xFFFFFFFF);
        context.pose().popPose();
    }
}
