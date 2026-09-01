package com.ro80t.betterui.compat.fabric.v1218;

import lombok.extern.slf4j.Slf4j;
import com.ro80t.betterui.compat.fabric.common.ICompatFabric;

@Slf4j
public class CompatFabric1218 implements ICompatFabric {
    public static final String VERSION = "1.21.8";

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void init() {
        log.info("Initializing Fabric 1.21.8 compatibility layer...");

        DurabilityHud.register();

        log.info("Successfully initialized Fabric 1.21.8 compatibility layer");
    }
}
