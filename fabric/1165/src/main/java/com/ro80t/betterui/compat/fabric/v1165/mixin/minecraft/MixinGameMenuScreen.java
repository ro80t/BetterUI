package com.ro80t.betterui.compat.fabric.v1165.mixin.minecraft;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a pause menu button that toggles the armor durability HUD on/off and
 * immediately persists the change to the config file.
 */
@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {
    protected MixinGameMenuScreen(final Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void betterui$addBetterUiButton(final CallbackInfo ci) {
        this.addButton(new ButtonWidget(4, this.height - 24, 150, 20, betterui$durabilityHudLabel(), button -> {
            BetterUiMod.toggleDurabilityHud();
            button.setMessage(betterui$durabilityHudLabel());
        }));
    }

    private static Text betterui$durabilityHudLabel() {
        final String state = BetterUiMod.getConfig().isDurabilityHudEnabled() ? "ON" : "OFF";
        return new LiteralText("Durability HUD: " + state);
    }
}
