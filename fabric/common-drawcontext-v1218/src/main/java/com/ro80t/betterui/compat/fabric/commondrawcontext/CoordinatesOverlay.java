package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Draws the player's coordinates, rounded to one decimal place, as a small
 * line in the top-left corner of the screen, below the FPS display. Toggle
 * with {@code coordinatesDisplayEnabled} in the mod's config file.
 * <p>
 * Byte-identical to {@code fabric:common-drawcontext}'s copy, but compiled
 * separately against 1218 mappings because {@code DrawContext.drawTextWithShadow(...)}
 * changed its return type from {@code int} to {@code void} in that version -
 * see {@link ArmorDurabilityOverlay} for why. Used only by the 1218 Fabric
 * module.
 */
public final class CoordinatesOverlay {
    private static final int MARGIN = 2;
    private static final int ROW_Y = MARGIN + 10;

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
        context.drawTextWithShadow(client.textRenderer, text, MARGIN, ROW_Y, 0xFFFFFFFF);
    }
}
