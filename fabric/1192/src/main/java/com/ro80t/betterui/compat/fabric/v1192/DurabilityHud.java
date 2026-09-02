package com.ro80t.betterui.compat.fabric.v1192;

import com.ro80t.betterui.compat.fabric.commonlegacy.ArmorDurabilityOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * Registers {@link ArmorDurabilityOverlay} on the HUD render callback.
 */
public final class DurabilityHud {
    private DurabilityHud() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> ArmorDurabilityOverlay.render(matrices));
    }
}
