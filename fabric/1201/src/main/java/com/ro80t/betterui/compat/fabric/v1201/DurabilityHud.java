package com.ro80t.betterui.compat.fabric.v1201;

import com.ro80t.betterui.compat.fabric.commondrawcontext.ArmorDurabilityOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

/**
 * Registers {@link ArmorDurabilityOverlay} on the HUD render callback.
 */
public final class DurabilityHud {
    private DurabilityHud() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register(DurabilityHud::render);
    }

    private static void render(final DrawContext context, final float tickDelta) {
        ArmorDurabilityOverlay.render(context);
        FpsOverlay.render(context);
        CoordinatesOverlay.render(context);
    }
}
