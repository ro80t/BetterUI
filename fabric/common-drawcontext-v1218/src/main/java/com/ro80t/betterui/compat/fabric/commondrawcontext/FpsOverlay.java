package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

/**
 * Draws the current FPS as a small number, at a position and scale editable
 * via the BetterUI position editor screen ({@code fpsLayout} in the config).
 * <p>
 * Byte-identical to {@code fabric:common-drawcontext}'s copy, but compiled
 * separately against 1218 mappings because {@code DrawContext.drawTextWithShadow(...)}
 * changed its return type from {@code int} to {@code void} in that version -
 * see {@link ArmorDurabilityOverlay} for why. Used only by the 1218 Fabric
 * module.
 */
public final class FpsOverlay {
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

        final HudLayout layout = BetterUiMod.getConfig().getFpsLayout();
        final Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(layout.x(context.getScaledWindowWidth()), layout.y(context.getScaledWindowHeight()));
        matrices.scale(layout.getScale());
        context.drawTextWithShadow(client.textRenderer, client.getCurrentFps() + " FPS", 0, 0, 0xFFFFFFFF);
        matrices.popMatrix();
    }
}
