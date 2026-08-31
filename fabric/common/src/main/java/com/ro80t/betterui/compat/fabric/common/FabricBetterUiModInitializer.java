package com.ro80t.betterui.compat.fabric.common;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import com.ro80t.betterui.BetterUiMod;

@Slf4j
public final class FabricBetterUiModInitializer implements ModInitializer {
    public FabricBetterUiModInitializer() {
        log.info(BetterUiMod.MOD_NAME + " is being constructed");
    }

    @Override
    public void onInitialize() {
        log.info(BetterUiMod.MOD_NAME + " initializing...");

        if (((IFabricBetterUiMod) BetterUiMod.getInstance()).getCompat() == null) {
            throw new UnsupportedOperationException("The version is not supported by " + BetterUiMod.MOD_NAME);
        } else {
            log.debug("The version is supported by " + BetterUiMod.MOD_NAME);
        }

        log.info("Successfully " + BetterUiMod.MOD_NAME + " initialized");
    }
}
