package com.ro80t.betterui.compat.fabric.commonlegacy.mixin.minecraft;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * "Durability show": draws the remaining durability as a small number in the
 * top-left corner of every rendered item slot (hotbar, inventory, etc.),
 * colored the same as vanilla's own durability bar.
 * <p>
 * Shared by 1182/1192; 1165 predates {@code Item.getItemBarColor} and keeps
 * its own copy with a manually computed color.
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRendererDurability {
    @Inject(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("TAIL")
    )
    private void betterui$drawDurability(final TextRenderer textRenderer, final ItemStack stack,
                                          final int x, final int y, final CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }

        final Item item = stack.getItem();
        if (!item.isItemBarVisible(stack)) {
            return;
        }

        final int remaining = stack.getMaxDamage() - stack.getDamage();
        final int color = item.getItemBarColor(stack) | 0xFF000000;

        textRenderer.drawWithShadow(new MatrixStack(), String.valueOf(remaining), x + 1, y + 1, color);
    }
}
