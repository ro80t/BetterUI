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
         * Example UI-improvement handler: adds a small "BetterUI" button to the
         * bottom-left corner of the pause menu. Replace the press action with a
         * real feature.
         */
        @SubscribeEvent
        public static void onScreenInit(final ScreenEvent.Init.Post event) {
            if (!(event.getScreen() instanceof PauseScreen screen)) {
                return;
            }

            event.addListener(Button.builder(Component.literal("BetterUI"), button -> {
                    })
                    .bounds(4, screen.height - 24, 60, 20)
                    .build());
        }
    }
}
