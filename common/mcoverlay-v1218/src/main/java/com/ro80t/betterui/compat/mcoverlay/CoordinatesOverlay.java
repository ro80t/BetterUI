package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Matrix3x2fStack;

/**
 * Draws the player's coordinates, rounded to one decimal place, at a
 * position and scale editable via the BetterUI position editor screen
 * ({@code coordinatesLayout} in the config).
 * <p>
 * Byte-identical in intent to {@code common:mcoverlay}'s copy, but compiled
 * separately against 1.21.8 mappings and using {@link Matrix3x2fStack}
 * instead of {@code PoseStack} - see {@link ArmorDurabilityOverlay} for why.
 * Used only by the 1218 Forge/NeoForge modules.
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
        final Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        matrices.translate(layout.x(context.guiWidth()), layout.y(context.guiHeight()));
        matrices.scale(layout.getScale());
        context.drawString(client.font, text, 0, 0, 0xFFFFFFFF);
        matrices.popMatrix();
    }
}
