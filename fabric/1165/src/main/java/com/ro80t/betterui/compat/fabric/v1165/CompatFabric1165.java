package com.ro80t.betterui.compat.fabric.v1165;

import lombok.extern.slf4j.Slf4j;
import com.ro80t.betterui.compat.fabric.common.ICompatFabric;

@Slf4j
public class CompatFabric1165 implements ICompatFabric {
    public static final String VERSION = "1.16.5";

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void init() {
        log.info("Initializing Fabric 1.16.5 compatibility layer...");

        DurabilityHud.register();

        log.info("Successfully initialized Fabric 1.16.5 compatibility layer");
    }
}
