package com.ro80t.betterui.compat.neoforge.common;

import com.ro80t.betterui.compat.mcoverlay.ArmorDurabilityOverlay;
import com.ro80t.betterui.compat.mcoverlay.FpsOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Renders {@link ArmorDurabilityOverlay} on top of the HUD. Shared by every
 * NeoForge version using the flat {@code @EventBusSubscriber} annotation
 * (1211/1214/1218).
 */
@EventBusSubscriber(Dist.CLIENT)
public final class DurabilityHud {
    private DurabilityHud() {
    }

    @SubscribeEvent
    public static void onRenderGui(final RenderGuiEvent.Post event) {
        ArmorDurabilityOverlay.render(event.getGuiGraphics());
        FpsOverlay.render(event.getGuiGraphics());
    }
}
