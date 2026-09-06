package com.ro80t.betterui.compat.fabric.v1192;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.fabric.commonlegacy.BetterUiDurabilityItemEditorScreen;
import com.ro80t.betterui.compat.fabric.commonlegacy.BetterUiPositionEditorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * BetterUI's own settings screen: one toggle button per entry in
 * {@link BetterUiMod#toggleSettings()}, plus a "Done" button back to
 * whichever screen opened this one (usually the pause menu).
 */
public final class BetterUiSettingsScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 24;

    private final Screen parent;

    public BetterUiSettingsScreen(final Screen parent) {
        super(Text.literal("BetterUI Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final int centerX = this.width / 2 - BUTTON_WIDTH / 2;
        int y = this.height / 4;

        for (final BetterUiMod.ToggleSetting setting : BetterUiMod.toggleSettings()) {
            this.addDrawableChild(new ButtonWidget(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT, betterui$label(setting), button -> {
                setting.toggle();
                button.setMessage(betterui$label(setting));
            }));
            y += BUTTON_SPACING;
        }

        this.addDrawableChild(new ButtonWidget(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Edit HUD Positions"), button -> this.client.setScreen(new BetterUiPositionEditorScreen(this))));
        y += BUTTON_SPACING;

        this.addDrawableChild(new ButtonWidget(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Edit Durability Number"), button -> this.client.setScreen(new BetterUiDurabilityItemEditorScreen(this))));

        this.addDrawableChild(new ButtonWidget(centerX, this.height - 28, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Done"), button -> this.close()));
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private static Text betterui$label(final BetterUiMod.ToggleSetting setting) {
        return Text.literal(setting.label() + ": " + (setting.isEnabled() ? "ON" : "OFF"));
    }
}
