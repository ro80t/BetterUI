package com.ro80t.betterui.compat.fabric.v1165;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
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
        super(new LiteralText("BetterUI Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final int centerX = this.width / 2 - BUTTON_WIDTH / 2;
        int y = this.height / 4;

        for (final BetterUiMod.ToggleSetting setting : BetterUiMod.toggleSettings()) {
            this.addButton(new ButtonWidget(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT, betterui$label(setting), button -> {
                setting.toggle();
                button.setMessage(betterui$label(setting));
            }));
            y += BUTTON_SPACING;
        }

        this.addButton(new ButtonWidget(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                new LiteralText("Edit HUD Positions"), button -> this.client.openScreen(new BetterUiPositionEditorScreen(this))));
        y += BUTTON_SPACING;

        this.addButton(new ButtonWidget(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                new LiteralText("Edit Durability Number"), button -> this.client.openScreen(new BetterUiDurabilityItemEditorScreen(this))));

        this.addButton(new ButtonWidget(centerX, this.height - 28, BUTTON_WIDTH, BUTTON_HEIGHT,
                new LiteralText("Done"), button -> this.onClose()));
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    private static Text betterui$label(final BetterUiMod.ToggleSetting setting) {
        return new LiteralText(setting.label() + ": " + (setting.isEnabled() ? "ON" : "OFF"));
    }
}
