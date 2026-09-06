package com.ro80t.betterui.impl.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Screen position and scale for one draggable HUD element, edited via the
 * BetterUI position editor screen.
 * <p>
 * Position is an anchor corner ({@code anchorX}/{@code anchorY}, each 0.0-1.0
 * as a fraction of the screen's width/height) plus a pixel offset from that
 * anchor, so an element stays pinned to the same corner/edge across window
 * resizes instead of drifting. Dragging only ever changes the pixel offset,
 * never the anchor.
 * <p>
 * The per-item durability number ({@code Config#getDurabilityItemLayout()})
 * is the one exception: it's drawn per item slot rather than at a fixed
 * screen position, so its anchor fields are unused and its offset is instead
 * a delta from that slot's own default position.
 */
@Getter
@Setter
public class HudLayout {
    private float anchorX;
    private float anchorY;
    private int offsetX;
    private int offsetY;
    private float scale = 1.0F;

    public static HudLayout of(final float anchorX, final float anchorY, final int offsetX, final int offsetY) {
        return of(anchorX, anchorY, offsetX, offsetY, 1.0F);
    }

    public static HudLayout of(final float anchorX, final float anchorY, final int offsetX, final int offsetY,
                               final float scale) {
        final HudLayout layout = new HudLayout();
        layout.anchorX = anchorX;
        layout.anchorY = anchorY;
        layout.offsetX = offsetX;
        layout.offsetY = offsetY;
        layout.scale = scale;
        return layout;
    }

    /**
     * Overwrites every field in place, so references already held elsewhere
     * (e.g. by the position editor screen) keep seeing the new values.
     */
    public void set(final float anchorX, final float anchorY, final int offsetX, final int offsetY,
                     final float scale) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
    }

    /**
     * Resolves this layout's anchor+offset against the given screen size to
     * an absolute pixel position.
     */
    public int x(final int screenWidth) {
        return Math.round(anchorX * screenWidth) + offsetX;
    }

    public int y(final int screenHeight) {
        return Math.round(anchorY * screenHeight) + offsetY;
    }
}
