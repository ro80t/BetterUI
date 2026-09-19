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

    /**
     * Screen position/scale of the FPS display. Edited via the BetterUI
     * position editor screen (drag to move, +/- buttons to resize).
     */
    private HudLayout fpsLayout = HudLayout.of(0.0F, 0.0F, 2, 2);

    /**
     * Screen position/scale of the coordinates display.
     */
    private HudLayout coordinatesLayout = HudLayout.of(0.0F, 0.0F, 2, 12);

    /**
     * Screen position/scale of the armor durability HUD panel.
     */
    private HudLayout armorHudLayout = HudLayout.of(1.0F, 1.0F, -22, -22);

    /**
     * Lays out the armor durability HUD's rows side-by-side instead of
     * stacked, when {@code true} they stack vertically (the original
     * layout); {@code false} lines them up horizontally instead.
     */
    private boolean armorHudVertical = true;

    /**
     * Position/scale of the per-item-slot durability number. Unlike the
     * other layouts, {@code anchorX}/{@code anchorY} are unused here - the
     * offset is a delta from each item slot's own default position, since
     * this isn't drawn at one fixed screen location.
     */
    private HudLayout durabilityItemLayout = HudLayout.of(0.0F, 0.0F, 0, 0, 0.5F);

    /**
     * Each resets one HUD element's position/scale back to its default
     * value, in place, so any long-lived references to the {@link HudLayout}
     * instance stay valid. Used by each row's own "Reset" button on the
     * BetterUI position editor screen.
     */
    public void resetFpsLayout() {
        this.fpsLayout.set(0.0F, 0.0F, 2, 2, 1.0F);
    }

    public void resetCoordinatesLayout() {
        this.coordinatesLayout.set(0.0F, 0.0F, 2, 12, 1.0F);
    }

    public void resetArmorHudLayout() {
        this.armorHudLayout.set(1.0F, 1.0F, -22, -22, 1.0F);
    }

    public void resetDurabilityItemLayout() {
        this.durabilityItemLayout.set(0.0F, 0.0F, 0, 0, 0.5F);
    }

    /**
     * Resets every HUD position/scale/orientation setting back to its
     * default value. Used by the "Reset to Default" button on the BetterUI
     * position editor screen.
     */
    public void resetHudLayouts() {
        resetFpsLayout();
        resetCoordinatesLayout();
        resetArmorHudLayout();
        this.armorHudVertical = true;
        resetDurabilityItemLayout();
    }
}
