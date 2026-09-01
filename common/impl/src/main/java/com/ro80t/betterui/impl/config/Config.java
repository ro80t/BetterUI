package com.ro80t.betterui.impl.config;

import lombok.Getter;
import lombok.Setter;
import com.ro80t.betterui.api.config.IConfig;

@Setter
@Getter
public class Config implements IConfig {
    /**
     * Shows remaining armor durability as a small HUD panel in the bottom-right
     * corner of the screen. Toggle by editing the mod's config JSON file
     * (see {@link ConfigIo}) and restarting.
     */
    private boolean durabilityHudEnabled = true;
}
