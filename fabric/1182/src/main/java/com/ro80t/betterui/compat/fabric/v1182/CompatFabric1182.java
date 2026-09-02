package com.ro80t.betterui.compat.fabric.v1182;

import lombok.extern.slf4j.Slf4j;
import com.ro80t.betterui.compat.fabric.common.ICompatFabric;

@Slf4j
public class CompatFabric1182 implements ICompatFabric {
    public static final String VERSION = "1.18.2";

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void init() {
        log.info("Initializing Fabric 1.18.2 compatibility layer...");

        DurabilityHud.register();

        log.info("Successfully initialized Fabric 1.18.2 compatibility layer");
    }
}
