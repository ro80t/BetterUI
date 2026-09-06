package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

/**
 * Draws the current FPS as a small number, at a position and scale editable
 * via the BetterUI position editor screen ({@code fpsLayout} in the config).
 * <p>
 * Byte-identical in intent to {@code common:mcoverlay}'s copy, but compiled
 * separately against 1.21.8 mappings and using {@link Matrix3x2fStack}
 * instead of {@code PoseStack} - see {@link ArmorDurabilityOverlay} for why.
 * Used only by the 1218 Forge/NeoForge modules.
 */
public final class FpsOverlay {
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

        final HudLayout layout = BetterUiMod.getConfig().getFpsLayout();
        final Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        matrices.translate(layout.x(context.guiWidth()), layout.y(context.guiHeight()));
        matrices.scale(layout.getScale());
        context.drawString(client.font, client.getFps() + " FPS", 0, 0, 0xFFFFFFFF);
        matrices.popMatrix();
    }
}
