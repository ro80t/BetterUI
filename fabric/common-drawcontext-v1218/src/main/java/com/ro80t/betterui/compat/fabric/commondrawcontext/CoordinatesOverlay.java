package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import org.joml.Matrix3x2fStack;

/**
 * Draws the player's coordinates, rounded to one decimal place, at a
 * position and scale editable via the BetterUI position editor screen
 * ({@code coordinatesLayout} in the config).
 * <p>
 * Byte-identical to {@code fabric:common-drawcontext}'s copy, but compiled
 * separately against 1218 mappings because {@code DrawContext.drawTextWithShadow(...)}
 * changed its return type from {@code int} to {@code void} in that version -
 * see {@link ArmorDurabilityOverlay} for why. Used only by the 1218 Fabric
 * module.
 */
public final class CoordinatesOverlay {
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

        final HudLayout layout = BetterUiMod.getConfig().getCoordinatesLayout();
        final Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(layout.x(context.getScaledWindowWidth()), layout.y(context.getScaledWindowHeight()));
        matrices.scale(layout.getScale());
        context.drawTextWithShadow(client.textRenderer, text, 0, 0, 0xFFFFFFFF);
        matrices.popMatrix();
    }
}
