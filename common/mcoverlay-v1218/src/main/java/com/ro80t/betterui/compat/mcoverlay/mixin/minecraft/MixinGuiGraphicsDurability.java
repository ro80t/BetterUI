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
 * "Durability show": draws the remaining durability as a small number in the
 * top-left corner of every rendered item slot (hotbar, inventory, etc.),
 * colored the same as vanilla's own durability bar.
 * <p>
 * Byte-identical to {@code common:mcoverlay}'s copy, but compiled separately
 * against 1.21.8 mappings - see {@link com.ro80t.betterui.compat.mcoverlay.ArmorDurabilityOverlay}
 * for why. Used only by the 1218 Forge/NeoForge modules.
 */
@Mixin(value = GuiGraphics.class, remap = false)
public abstract class MixinGuiGraphicsDurability {
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
        final int remaining = stack.getMaxDamage() - stack.getDamageValue();
        final int color = item.getBarColor(stack) | 0xFF000000;

        self.drawString(font, String.valueOf(remaining), x + 1, y + 1, color);
    }
}
