package com.ro80t.betterui.compat.mcoverlay.mixin.minecraft;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
 * Byte-identical in intent to {@code common:mcoverlay}'s copy, but compiled
 * separately against 1.21.8 mappings and using {@link Matrix3x2fStack}
 * instead of {@code PoseStack} - see {@link com.ro80t.betterui.compat.mcoverlay.ArmorDurabilityOverlay}
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
        if (!BetterUiMod.getConfig().isDurabilityShowEnabled()) {
            return;
        }

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
        final HudLayout layout = BetterUiMod.getConfig().getDurabilityItemLayout();

        final Matrix3x2fStack matrices = self.pose();
        matrices.pushMatrix();
        matrices.translate(x + 8.0F + layout.getOffsetX(), y + 8.0F + layout.getOffsetY());
        matrices.scale(layout.getScale());
        self.drawString(font, text, -textWidth / 2, 0, color);
        matrices.popMatrix();
    }
}
