package com.ro80t.betterui.compat.fabric.commondrawcontext.mixin.minecraft;

import com.ro80t.betterui.BetterUiMod;
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
 * <p>
 * Shared by every Fabric version whose {@code DrawContext} names this method
 * {@code drawStackOverlay} and still exposes {@code getMatrices()} as a
 * {@code MatrixStack} (1211/1214). 1201 still calls it {@code drawItemInSlot}
 * and keeps its own copy; 1218 switched {@code getMatrices()} to a JOML
 * {@code Matrix3x2fStack} and keeps its own copy in {@code fabric:common-drawcontext-v1218}.
 */
@Mixin(DrawContext.class)
public abstract class MixinDrawContextDurability {
    private static final float SCALE = 0.5F;

    @Inject(
            method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL")
    )
    private void betterui$drawDurability(final TextRenderer textRenderer, final ItemStack stack,
                                          final int x, final int y, final String countOverride,
                                          final CallbackInfo ci) {
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
