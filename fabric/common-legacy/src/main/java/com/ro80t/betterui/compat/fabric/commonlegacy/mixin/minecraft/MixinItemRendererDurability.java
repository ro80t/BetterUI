package com.ro80t.betterui.compat.fabric.commonlegacy.mixin.minecraft;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
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
 * "Durability show": draws the remaining durability as a small number,
 * scaled down, just above the item's own durability bar in every rendered
 * item slot (hotbar, inventory, etc.).
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
        if (!BetterUiMod.getConfig().isDurabilityShowEnabled()) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        final Item item = stack.getItem();
        if (!item.isItemBarVisible(stack)) {
            return;
        }

        final String text = String.valueOf(stack.getMaxDamage() - stack.getDamage());
        final int color = item.getItemBarColor(stack) | 0xFF000000;
        final int textWidth = textRenderer.getWidth(text);
        final HudLayout layout = BetterUiMod.getConfig().getDurabilityItemLayout();

        final MatrixStack matrices = new MatrixStack();
        matrices.push();
        // Match vanilla's own item-count overlay: without this Z push, the text
        // gets depth-tested behind the item's own 3D icon render and vanishes.
        matrices.translate(x + 8.0F + layout.getOffsetX(), y + 8.0F + layout.getOffsetY(), 200.0F);
        matrices.scale(layout.getScale(), layout.getScale(), 1.0F);
        textRenderer.drawWithShadow(matrices, text, -textWidth / 2F, 0, color);
        matrices.pop();
    }
}
