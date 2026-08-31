package com.ro80t.betterui.compat.fabric.v1201;

import lombok.extern.slf4j.Slf4j;
import com.ro80t.betterui.compat.fabric.common.ICompatFabric;

@Slf4j
public class CompatFabric1201 implements ICompatFabric {
    public static final String VERSION = "1.20.1";

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void init() {
        log.info("Initializing Fabric 1.20.1 compatibility layer...");

        log.info("Successfully initialized Fabric 1.20.1 compatibility layer");
    }
}
