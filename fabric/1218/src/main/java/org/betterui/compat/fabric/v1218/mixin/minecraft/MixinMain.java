package org.betterui.compat.fabric.v1218.mixin.minecraft;

import net.minecraft.MinecraftVersion;
import net.minecraft.client.main.Main;
import org.betterui.BetterUiMod;
import org.betterui.compat.fabric.common.ICompatFabric;
import org.betterui.compat.fabric.common.IFabricBetterUiMod;
import org.betterui.compat.fabric.v1218.CompatFabric1218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = Main.class, remap = false)
public class MixinMain {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Ljoptsimple/OptionParser;<init>()V"))
    private static void main(final String[] args, final CallbackInfo ci) {
        final ICompatFabric compat = new CompatFabric1218();
        if (Objects.equals(MinecraftVersion.CURRENT.name(), compat.getVersion())) {
            compat.init();
            ((IFabricBetterUiMod) BetterUiMod.getInstance()).setCompat(compat);
        }
    }
}
