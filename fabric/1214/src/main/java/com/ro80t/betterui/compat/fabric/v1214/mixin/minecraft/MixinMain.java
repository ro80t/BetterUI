package com.ro80t.betterui.compat.fabric.v1214.mixin.minecraft;

import net.minecraft.MinecraftVersion;
import net.minecraft.client.main.Main;
import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.compat.fabric.common.ICompatFabric;
import com.ro80t.betterui.compat.fabric.common.IFabricBetterUiMod;
import com.ro80t.betterui.compat.fabric.v1214.CompatFabric1214;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = Main.class, remap = false)
public class MixinMain {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Ljoptsimple/OptionParser;<init>()V"))
    private static void main(final String[] args, final CallbackInfo ci) {
        final ICompatFabric compat = new CompatFabric1214();
        if (Objects.equals(MinecraftVersion.CURRENT.getName(), compat.getVersion())) {
            compat.init();
            ((IFabricBetterUiMod) BetterUiMod.getInstance()).setCompat(compat);
        }
    }
}
