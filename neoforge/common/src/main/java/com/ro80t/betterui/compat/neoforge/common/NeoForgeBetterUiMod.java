package com.ro80t.betterui.compat.neoforge.common;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.api.IBetterUiMod;
import com.ro80t.betterui.compat.mcoverlay.BetterUiSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Shared by every NeoForge version using the flat {@code @EventBusSubscriber}
 * annotation (1211/1214/1218). 1204 predates it and keeps its own copy with
 * the older, Forge-style nested {@code @Mod.EventBusSubscriber}.
 */
@Mod("betterui")
public final class NeoForgeBetterUiMod implements IBetterUiMod {
    public NeoForgeBetterUiMod(final IEventBus eventBus, final ModContainer modContainer) {
        BetterUiMod.initialize(this, FMLPaths.CONFIGDIR.get());
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientModEvents {
        /**
         * Adds a pause menu button that opens the BetterUI settings screen.
         */
        @SubscribeEvent
        public static void onScreenInit(final ScreenEvent.Init.Post event) {
            if (!(event.getScreen() instanceof PauseScreen screen)) {
                return;
            }

            event.addListener(Button.builder(Component.literal("BetterUI Settings"),
                            button -> Minecraft.getInstance().setScreen(new BetterUiSettingsScreen(screen)))
                    .bounds(4, screen.height - 24, 150, 20)
                    .build());
        }
    }
}
