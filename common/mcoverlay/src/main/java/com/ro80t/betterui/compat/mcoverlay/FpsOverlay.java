package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Draws the current FPS as a small number, at a position and scale editable
 * via the BetterUI position editor screen ({@code fpsLayout} in the config).
 * <p>
 * Vanilla-only, shared unchanged by every Forge and NeoForge version except
 * 1218, which uses {@code common:mcoverlay-v1218}'s own copy - see
 * {@link ArmorDurabilityOverlay} for why.
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
        context.pose().pushPose();
        context.pose().translate(layout.x(context.guiWidth()), layout.y(context.guiHeight()), 0.0F);
        context.pose().scale(layout.getScale(), layout.getScale(), 1.0F);
        context.drawString(client.font, client.getFps() + " FPS", 0, 0, 0xFFFFFFFF);
        context.pose().popPose();
    }
}
