package com.ro80t.betterui.compat.fabric.v1218;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.fabric.commondrawcontext.BetterUiDurabilityItemEditorScreen;
import com.ro80t.betterui.compat.fabric.commondrawcontext.BetterUiPositionEditorScreen;
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
            this.addDrawableChild(ButtonWidget.builder(betterui$label(setting), button -> {
                        setting.toggle();
                        button.setMessage(betterui$label(setting));
                    })
                    .dimensions(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build());
            y += BUTTON_SPACING;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Edit HUD Positions"),
                        button -> this.client.setScreen(new BetterUiPositionEditorScreen(this)))
                .dimensions(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
        y += BUTTON_SPACING;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Edit Durability Number"),
                        button -> this.client.setScreen(new BetterUiDurabilityItemEditorScreen(this)))
                .dimensions(centerX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(centerX, this.height - 28, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private static Text betterui$label(final BetterUiMod.ToggleSetting setting) {
        return Text.literal(setting.label() + ": " + (setting.isEnabled() ? "ON" : "OFF"));
    }
}
