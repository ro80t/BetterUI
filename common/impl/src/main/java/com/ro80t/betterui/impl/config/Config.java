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
     * (see {@link ConfigIo}) and restarting, or in-game via the BetterUI
     * settings screen.
     */
    private boolean durabilityHudEnabled = true;

    /**
     * Shows remaining durability as a small number on every rendered item
     * slot icon (hotbar, inventory, anywhere an item is drawn). Toggle by
     * editing the mod's config JSON file (see {@link ConfigIo}) and
     * restarting, or in-game via the BetterUI settings screen.
     */
    private boolean durabilityShowEnabled = true;

    /**
     * Shows the current FPS as a small number in the top-left corner of the
     * screen. Toggle by editing the mod's config JSON file (see
     * {@link ConfigIo}) and restarting, or in-game via the BetterUI settings
     * screen.
     */
    private boolean fpsDisplayEnabled = true;

    /**
     * Shows the player's coordinates (rounded to one decimal place) as a
     * small line in the top-left corner of the screen, below the FPS
     * display. Toggle by editing the mod's config JSON file (see
     * {@link ConfigIo}) and restarting, or in-game via the BetterUI settings
     * screen.
     */
    private boolean coordinatesDisplayEnabled = true;
}
