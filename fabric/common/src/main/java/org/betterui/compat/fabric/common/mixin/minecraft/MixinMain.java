package org.betterui.compat.fabric.common.mixin.minecraft;

import net.minecraft.client.main.Main;
import org.betterui.BetterUiMod;
import org.betterui.compat.fabric.common.FabricBetterUiMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, remap = false)
public class MixinMain {
    @Inject(method = "main", at = @At("HEAD"))
    private static void main(final String[] args, final CallbackInfo ci) {
        BetterUiMod.setInstance(new FabricBetterUiMod());
    }
}
