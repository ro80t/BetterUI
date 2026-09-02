package com.ro80t.betterui.compat.forge.common;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.mcoverlay.ArmorDurabilityOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Registers {@link ArmorDurabilityOverlay} as a Forge GUI overlay.
 * 1.20.1 predates the {@code AddGuiOverlayLayersEvent}/{@code LayeredDraw}
 * system used from 1.20.5 onward, so this version alone still needs the
 * older {@code RegisterGuiOverlaysEvent}/{@code IGuiOverlay} registration.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "betterui", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DurabilityHud {
    private DurabilityHud() {
    }

    @SubscribeEvent
    public static void onRegisterOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(BetterUiMod.MOD_ID + ":durability_hud",
                (IGuiOverlay) (gui, context, partialTick, screenWidth, screenHeight) ->
                        ArmorDurabilityOverlay.render(context));
    }
}
