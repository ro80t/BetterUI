package com.ro80t.betterui.compat.fabric.v1211;

import com.ro80t.betterui.compat.fabric.commondrawcontext.ArmorDurabilityOverlay;
import com.ro80t.betterui.compat.fabric.commondrawcontext.CoordinatesOverlay;
import com.ro80t.betterui.compat.fabric.commondrawcontext.FpsOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Registers {@link ArmorDurabilityOverlay} on the HUD render callback.
 */
public final class DurabilityHud {
    private DurabilityHud() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register(DurabilityHud::render);
    }

    private static void render(final DrawContext context, final RenderTickCounter tickCounter) {
        ArmorDurabilityOverlay.render(context);
        FpsOverlay.render(context);
        CoordinatesOverlay.render(context);
    }
}
