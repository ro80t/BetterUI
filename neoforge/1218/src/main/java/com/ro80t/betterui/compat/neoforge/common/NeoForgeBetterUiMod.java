package com.ro80t.betterui.compat.neoforge.common;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ScreenEvent;
import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.api.IBetterUiMod;

@Slf4j
@Mod(value = "betterui", dist = Dist.CLIENT)
public final class NeoForgeBetterUiMod implements IBetterUiMod {
    public NeoForgeBetterUiMod(final IEventBus eventBus, final ModContainer modContainer) {
        BetterUiMod.setInstance(this);
        BetterUiMod.loadConfig(FMLPaths.CONFIGDIR.get());
        eventBus.addListener(this::onInitialize);
    }

    public void onInitialize(final FMLCommonSetupEvent event) {
        log.info(BetterUiMod.MOD_NAME + " initializing...");

        log.info(BetterUiMod.MOD_NAME + " initialized");
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
        }

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
