package com.ro80t.betterui.compat.fabric.v1211.mixin.minecraft;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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
 */
@Mixin(DrawContext.class)
public abstract class MixinDrawContextDurability {
    @Inject(
            method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
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
        final int remaining = stack.getMaxDamage() - stack.getDamage();
        final int color = item.getItemBarColor(stack) | 0xFF000000;

        self.drawTextWithShadow(textRenderer, String.valueOf(remaining), x + 1, y + 1, color);
    }
}
