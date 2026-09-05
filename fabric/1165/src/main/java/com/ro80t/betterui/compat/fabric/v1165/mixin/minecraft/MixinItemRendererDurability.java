package com.ro80t.betterui.compat.fabric.v1165.mixin.minecraft;

import com.ro80t.betterui.BetterUiMod;
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
 * "Durability show": draws the remaining durability as a small number,
 * scaled down, just above the item's own durability bar in every rendered
 * item slot (hotbar, inventory, etc.).
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRendererDurability {
    private static final float SCALE = 0.5F;

    @Inject(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("TAIL")
    )
    private void betterui$drawDurability(final TextRenderer textRenderer, final ItemStack stack,
                                          final int x, final int y, final CallbackInfo ci) {
        if (!BetterUiMod.getConfig().isDurabilityShowEnabled()) {
            return;
        }

        if (stack.isEmpty() || !stack.isDamaged()) {
            return;
        }

        final int remaining = stack.getMaxDamage() - stack.getDamage();
        final float durabilityFraction = Math.max(0.0F, (float) remaining / stack.getMaxDamage());
        final int color = MathHelper.hsvToRgb(durabilityFraction / 3.0F, 1.0F, 1.0F) | 0xFF000000;
        final String text = String.valueOf(remaining);
        final int textWidth = textRenderer.getWidth(text);

        final MatrixStack matrices = new MatrixStack();
        matrices.push();
        // Match vanilla's own item-count overlay: without this Z push, the text
        // gets depth-tested behind the item's own 3D icon render and vanishes.
        matrices.translate(x + 8.0F, y + 8.0F, 200.0F);
        matrices.scale(SCALE, SCALE, 1.0F);
        textRenderer.drawWithShadow(matrices, text, -textWidth / 2F, 0, color);
        matrices.pop();
    }
}
