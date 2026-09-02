package com.ro80t.betterui.compat.fabric.v1165.mixin.minecraft;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * "Durability show": draws the remaining durability as a small number in the
 * top-left corner of every rendered item slot (hotbar, inventory, etc.),
 * colored the same as vanilla's own durability bar.
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRendererDurability {
    @Inject(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("TAIL")
    )
    private void betterui$drawDurability(final TextRenderer textRenderer, final ItemStack stack,
                                          final int x, final int y, final CallbackInfo ci) {
        if (stack.isEmpty() || !stack.isDamaged()) {
            return;
        }

        final int remaining = stack.getMaxDamage() - stack.getDamage();
        final float durabilityFraction = Math.max(0.0F, (float) remaining / stack.getMaxDamage());
        final int color = MathHelper.hsvToRgb(durabilityFraction / 3.0F, 1.0F, 1.0F) | 0xFF000000;

        textRenderer.drawWithShadow(new MatrixStack(), String.valueOf(remaining), x + 1, y + 1, color);
    }
}
