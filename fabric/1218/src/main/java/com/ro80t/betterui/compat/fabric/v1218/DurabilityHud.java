package com.ro80t.betterui.compat.fabric.v1218;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.fabric.commondrawcontext.ArmorDurabilityOverlay;
import com.ro80t.betterui.compat.fabric.commondrawcontext.FpsOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/**
 * Registers {@link ArmorDurabilityOverlay} as a HUD element.
 */
public final class DurabilityHud {
    private DurabilityHud() {
    }

    public static void register() {
        HudElementRegistry.addLast(Identifier.of(BetterUiMod.MOD_ID, "durability_hud"), DurabilityHud::render);
    }

    private static void render(final DrawContext context, final RenderTickCounter tickCounter) {
        ArmorDurabilityOverlay.render(context);
        FpsOverlay.render(context);
    }
}
