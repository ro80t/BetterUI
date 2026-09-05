package com.ro80t.betterui.compat.neoforge.common;

import com.ro80t.betterui.compat.mcoverlay.ArmorDurabilityOverlay;
import com.ro80t.betterui.compat.mcoverlay.FpsOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Renders {@link ArmorDurabilityOverlay} on top of the HUD. Kept separate
 * from the 1211/1214/1218 copy because this early NeoForge version predates
 * the flat {@code @EventBusSubscriber} annotation and still needs the
 * Forge-style nested {@code @Mod.EventBusSubscriber}.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "betterui", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DurabilityHud {
    private DurabilityHud() {
    }

    @SubscribeEvent
    public static void onRenderGui(final RenderGuiEvent.Post event) {
        ArmorDurabilityOverlay.render(event.getGuiGraphics());
        FpsOverlay.render(event.getGuiGraphics());
    }
}
