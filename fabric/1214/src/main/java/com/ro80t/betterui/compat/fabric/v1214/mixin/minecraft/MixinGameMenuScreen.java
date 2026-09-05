package com.ro80t.betterui.compat.fabric.v1214.mixin.minecraft;

import com.ro80t.betterui.compat.fabric.v1214.BetterUiSettingsScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a pause menu button that opens the BetterUI settings screen.
 */
@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {
    protected MixinGameMenuScreen(final Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void betterui$addBetterUiButton(final CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("BetterUI Settings"),
                        button -> this.client.setScreen(new BetterUiSettingsScreen(this)))
                .dimensions(4, this.height - 24, 150, 20)
                .build());
    }
}
