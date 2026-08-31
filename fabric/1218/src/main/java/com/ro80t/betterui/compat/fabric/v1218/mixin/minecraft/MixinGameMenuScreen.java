package com.ro80t.betterui.compat.fabric.v1218.mixin.minecraft;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Example UI-improvement mixin: adds a small "BetterUI" button to the bottom-left
 * corner of the pause menu. Replace the press action with a real feature.
 */
@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {
    protected MixinGameMenuScreen(final Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void betterui$addBetterUiButton(final CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("BetterUI"), button -> {
                })
                .dimensions(4, this.height - 24, 60, 20)
                .build());
    }
}
