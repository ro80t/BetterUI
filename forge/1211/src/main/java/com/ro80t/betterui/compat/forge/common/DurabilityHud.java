package com.ro80t.betterui.compat.forge.common;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.mcoverlay.ArmorDurabilityOverlay;
import com.ro80t.betterui.compat.mcoverlay.CoordinatesOverlay;
import com.ro80t.betterui.compat.mcoverlay.FpsOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Registers {@link ArmorDurabilityOverlay} as a layer in vanilla's
 * {@code LayeredDraw} GUI system (Forge 1.20.5+).
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "betterui", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DurabilityHud {
    private DurabilityHud() {
    }

    @SubscribeEvent
    public static void onAddOverlays(final AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(ResourceLocation.fromNamespaceAndPath(BetterUiMod.MOD_ID, "durability_hud"),
                (context, deltaTracker) -> {
                    ArmorDurabilityOverlay.render(context);
                    FpsOverlay.render(context);
                    CoordinatesOverlay.render(context);
                });
    }
}
