package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Draws the current FPS as a small number, at a position and scale editable
 * via the BetterUI position editor screen ({@code fpsLayout} in the config).
 * <p>
 * Shared by every Fabric version whose {@code DrawContext} still exposes
 * {@code drawTextWithShadow(...)} returning {@code int} (1211/1214). 1218
 * changed that to {@code void} and keeps its own copy in
 * {@code fabric:common-drawcontext-v1218} - see
 * {@link ArmorDurabilityOverlay} for why.
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
        final MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(layout.x(context.getScaledWindowWidth()), layout.y(context.getScaledWindowHeight()), 0.0F);
        matrices.scale(layout.getScale(), layout.getScale(), 1.0F);
        context.drawTextWithShadow(client.textRenderer, client.getCurrentFps() + " FPS", 0, 0, 0xFFFFFFFF);
        matrices.pop();
    }
}
