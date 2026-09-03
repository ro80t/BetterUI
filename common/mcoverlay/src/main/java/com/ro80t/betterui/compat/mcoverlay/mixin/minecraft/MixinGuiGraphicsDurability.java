package com.ro80t.betterui.compat.mcoverlay.mixin.minecraft;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * "Durability show": draws the remaining durability as a small number,
 * scaled down, just above the item's own durability bar in every rendered
 * item slot (hotbar, inventory, etc.).
 * <p>
 * Vanilla-only, shared unchanged by every Forge and NeoForge version except
 * 1218, which uses {@code common:mcoverlay-v1218}'s own copy - see
 * {@link com.ro80t.betterui.compat.mcoverlay.ArmorDurabilityOverlay} for why.
 */
@Mixin(value = GuiGraphics.class, remap = false)
public abstract class MixinGuiGraphicsDurability {
    private static final float SCALE = 0.5F;

    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL")
    )
    private void betterui$drawDurability(final Font font, final ItemStack stack,
                                          final int x, final int y, final String countOverride,
                                          final CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }

        final Item item = stack.getItem();
        if (!item.isBarVisible(stack)) {
            return;
        }

        final GuiGraphics self = (GuiGraphics) (Object) this;
        final String text = String.valueOf(stack.getMaxDamage() - stack.getDamageValue());
        final int color = item.getBarColor(stack) | 0xFF000000;
        final int textWidth = font.width(text);

        self.pose().pushPose();
        self.pose().translate(x + 8.0F, y + 8.0F, 0.0F);
        self.pose().scale(SCALE, SCALE, 1.0F);
        self.drawString(font, text, -textWidth / 2, 0, color);
        self.pose().popPose();
    }
}
