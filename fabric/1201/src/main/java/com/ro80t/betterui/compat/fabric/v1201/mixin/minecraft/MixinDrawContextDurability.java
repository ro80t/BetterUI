package com.ro80t.betterui.compat.fabric.v1201.mixin.minecraft;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * "Durability show": draws the remaining durability as a small number,
 * scaled down, just above the item's own durability bar in every rendered
 * item slot (hotbar, inventory, etc.).
 */
@Mixin(DrawContext.class)
public abstract class MixinDrawContextDurability {
    private static final float SCALE = 0.5F;

    @Inject(
            method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL")
    )
    private void betterui$drawDurability(final TextRenderer textRenderer, final ItemStack stack,
                                          final int x, final int y, final String countOverride,
                                          final CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }

        final Item item = stack.getItem();
        if (!item.isItemBarVisible(stack)) {
            return;
        }

        final DrawContext self = (DrawContext) (Object) this;
        final String text = String.valueOf(stack.getMaxDamage() - stack.getDamage());
        final int color = item.getItemBarColor(stack) | 0xFF000000;
        final int textWidth = textRenderer.getWidth(text);

        final MatrixStack matrices = self.getMatrices();
        matrices.push();
        // Match vanilla's own item-count overlay: without this Z push, the text
        // gets depth-tested behind the item's own 3D icon render and vanishes.
        matrices.translate(x + 8.0F, y + 8.0F, 200.0F);
        matrices.scale(SCALE, SCALE, 1.0F);
        self.drawTextWithShadow(textRenderer, text, -textWidth / 2, 0, color);
        matrices.pop();
    }
}
