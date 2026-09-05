package com.ro80t.betterui.compat.fabric.v1165.mixin.minecraft;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes {@code MinecraftClient.currentFps}, which this version never got a
 * public getter for (that was added later - see {@code v1201}'s
 * {@code getCurrentFps()}), so BetterUI's FPS display can read it.
 */
@Mixin(MinecraftClient.class)
public interface MixinMinecraftClientFpsAccessor {
    @Accessor("currentFps")
    static int betterui$getCurrentFps() {
        throw new AssertionError();
    }
}
