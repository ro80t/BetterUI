package com.ro80t.betterui.compat.forge.common;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.api.IBetterUiMod;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Kept separate from {@code forge:common} because Forge's EventBus 7 rewrite
 * moved {@code SubscribeEvent} to a new package starting with this version.
 */
@Mod("betterui")
public final class ForgeBetterUiMod implements IBetterUiMod {
    public ForgeBetterUiMod(final FMLJavaModLoadingContext context) {
        BetterUiMod.initialize(this, FMLPaths.CONFIGDIR.get());
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "betterui", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientModEvents {
        /**
         * Adds a pause menu button that toggles the armor durability HUD on/off
         * and immediately persists the change to the config file.
         */
        @SubscribeEvent
        public static void onScreenInit(final ScreenEvent.Init.Post event) {
            if (!(event.getScreen() instanceof PauseScreen screen)) {
                return;
            }

            event.addListener(Button.builder(durabilityHudLabel(), button -> {
                        BetterUiMod.toggleDurabilityHud();
                        button.setMessage(durabilityHudLabel());
                    })
                    .bounds(4, screen.height - 24, 150, 20)
                    .build());
        }

        private static Component durabilityHudLabel() {
            final String state = BetterUiMod.getConfig().isDurabilityHudEnabled() ? "ON" : "OFF";
            return Component.literal("Durability HUD: " + state);
        }
    }
}
