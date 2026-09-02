package com.ro80t.betterui.compat.forge.common.mixin.minecraft;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Mixin(value = Main.class, remap = false)
public class MixinMain {
    @Inject(method = "main", at = @At("HEAD"))
    private static void main(final String[] args, final CallbackInfo ci) {
        log.info("BetterUI Mixin bootstrap loaded (Forge)");
    }
}
