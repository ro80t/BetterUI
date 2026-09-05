package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * BetterUI's own settings screen: one toggle button per entry in
 * {@link BetterUiMod#toggleSettings()}, plus a "Done" button back to
 * whichever screen opened this one (usually the pause menu).
 * <p>
 * Byte-identical to {@code common:mcoverlay}'s copy - Screen/Button/Component
 * are unaffected by the 1.21.8 GuiGraphics changes that forced the
 * ArmorDurabilityOverlay/Mixin split, so this one is duplicated here only to
 * keep the module boundary consistent, not because the API actually differs.
 * Used only by the 1218 Forge/NeoForge modules.
 */
public final class BetterUiSettingsScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 24;

    private final Screen parent;

    public BetterUiSettingsScreen(final Screen parent) {
        super(Component.literal("BetterUI Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final int centerX = this.width / 2 - BUTTON_WIDTH / 2;
        int y = this.height / 4;

        for (final BetterUiMod.ToggleSetting setting : BetterUiMod.toggleSettings()) {
            this.addRenderableWidget(Button.builder(betterui$label(setting), button -> {
                        setting.toggle();
                        button.setMessage(betterui$label(setting));
                    })
                    .bounds(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build());
            y += BUTTON_SPACING;
        }

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(centerX, this.height - 28, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private static Component betterui$label(final BetterUiMod.ToggleSetting setting) {
        return Component.literal(setting.label() + ": " + (setting.isEnabled() ? "ON" : "OFF"));
    }
}
