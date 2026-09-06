package com.ro80t.betterui.compat.fabric.commondrawcontext.mixin.minecraft;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * "Durability show": draws the remaining durability as a small number,
 * scaled down, just above the item's own durability bar in every rendered
 * item slot (hotbar, inventory, etc.).
 * <p>
 * Byte-identical in intent to {@code fabric:common-drawcontext}'s copy, but
 * compiled separately against 1218 mappings and using {@link Matrix3x2fStack}
 * instead of {@code MatrixStack} - see {@link com.ro80t.betterui.compat.fabric.commondrawcontext.ArmorDurabilityOverlay}
 * for why. Used only by the 1218 Fabric module.
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
        final HudLayout layout = BetterUiMod.getConfig().getDurabilityItemLayout();

        final Matrix3x2fStack matrices = self.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x + 8.0F + layout.getOffsetX(), y + 8.0F + layout.getOffsetY());
        matrices.scale(layout.getScale());
        self.drawTextWithShadow(textRenderer, text, -textWidth / 2, 0, color);
        matrices.popMatrix();
    }
}
