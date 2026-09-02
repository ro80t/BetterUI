package com.ro80t.betterui.compat.forge.common;

import com.ro80t.betterui.BetterUiMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability in the bottom-right corner of the screen.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "betterui", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DurabilityHud {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    private static final int MARGIN = 6;
    private static final int ROW_HEIGHT = 18;
    private static final int ICON_SIZE = 16;

    private DurabilityHud() {
    }

    @SubscribeEvent
    public static void onRegisterOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(BetterUiMod.MOD_ID + ":durability_hud", (IGuiOverlay) DurabilityHud::render);
    }

    private static void render(final net.minecraftforge.client.gui.overlay.ForgeGui gui, final GuiGraphics context,
                                final float partialTick, final int screenWidth, final int screenHeight) {
        if (!BetterUiMod.getConfig().isDurabilityHudEnabled()) {
            return;
        }

        final Minecraft client = Minecraft.getInstance();
        final LocalPlayer player = client.player;
        if (player == null || client.screen != null) {
            return;
        }

        final Font font = client.font;

        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.isDamageableItem()) {
                continue;
            }

            final Item item = stack.getItem();
            final int remaining = stack.getMaxDamage() - stack.getDamageValue();
            final int color = item.getBarColor(stack) | 0xFF000000;
            final String text = remaining + "/" + stack.getMaxDamage();

            final int iconX = screenWidth - MARGIN - ICON_SIZE;
            final int iconY = screenHeight - MARGIN - ICON_SIZE - row * ROW_HEIGHT;
            final int textX = iconX - 4 - font.width(text);
            final int textY = iconY + (ICON_SIZE - font.lineHeight) / 2;

            context.renderItem(stack, iconX, iconY);
            context.drawString(font, text, textX, textY, color);

            row++;
        }
    }
}
