package com.ro80t.betterui.compat.fabric.commonlegacy;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability, at a position/scale editable via the BetterUI
 * position editor screen ({@code armorHudLayout} in the config), stacked
 * either vertically or horizontally depending on {@code armorHudVertical}.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * Shared by the pre-DrawContext Fabric versions that still expose
 * {@code Item.getItemBarColor}/{@code isItemBarVisible} (1182/1192). 1165
 * predates those too and keeps its own standalone copy.
 */
public final class ArmorDurabilityOverlay {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    static final int MAX_ROWS = ARMOR_SLOTS_BOTTOM_UP.length;
    static final int ICON_SIZE = 16;
    static final int ROW_HEIGHT = 18;
    private static final int COLUMN_GAP = 4;
    private static final String WIDTH_SAMPLE_TEXT = "9999/9999";

    private ArmorDurabilityOverlay() {
    }

    /**
     * Width reserved for one column in horizontal mode: wide enough for the
     * icon plus a generously-sized durability string, so columns never
     * overlap regardless of how long the actual "remaining/max" text is.
     * Fixed (not fitted to the real text) so the BetterUI position editor's
     * preview can reserve the exact same width and always match reality -
     * see {@link BetterUiPositionEditorScreen#drawArmorHudPreview}.
     */
    static int columnWidth(final TextRenderer textRenderer) {
        return ICON_SIZE + COLUMN_GAP + textRenderer.getWidth(WIDTH_SAMPLE_TEXT) + COLUMN_GAP;
    }

    public static void render(final MatrixStack matrices) {
        if (!BetterUiMod.getConfig().isDurabilityHudEnabled()) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        final PlayerEntity player = client.player;
        if (player == null || client.currentScreen != null) {
            return;
        }

        final HudLayout layout = BetterUiMod.getConfig().getArmorHudLayout();
        final boolean vertical = BetterUiMod.getConfig().isArmorHudVertical();
        final int screenWidth = client.getWindow().getScaledWidth();
        final int screenHeight = client.getWindow().getScaledHeight();

        final int columnWidth = columnWidth(client.textRenderer);

        matrices.push();
        matrices.translate(layout.x(screenWidth), layout.y(screenHeight), 0.0F);
        matrices.scale(layout.getScale(), layout.getScale(), 1.0F);

        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final DurabilityEntry entry = new DurabilityEntry(player.getEquippedStack(slot));
            if (!entry.isVisible()) {
                continue;
            }

            entry.draw(matrices, client.textRenderer, client.getItemRenderer(), iconPosition(row, vertical, columnWidth));
            row++;
        }

        matrices.pop();
    }

    static Position iconPosition(final int row, final boolean vertical, final int columnWidth) {
        return vertical ? new Position(0, -row * ROW_HEIGHT) : new Position(-row * columnWidth, 0);
    }

    record Position(int x, int y) {
    }

    private record DurabilityEntry(ItemStack stack) {
        boolean isVisible() {
            return !stack.isEmpty() && stack.isDamageable();
        }

        private int remaining() {
            return stack.getMaxDamage() - stack.getDamage();
        }

        private int color() {
            return stack.getItem().getItemBarColor(stack) | 0xFF000000;
        }

        private String text() {
            return remaining() + "/" + stack.getMaxDamage();
        }

        void draw(final MatrixStack matrices, final TextRenderer textRenderer,
                  final ItemRenderer itemRenderer, final Position iconPos) {
            final int textX = iconPos.x() - 4 - textRenderer.getWidth(text());
            final int textY = iconPos.y() + (ICON_SIZE - textRenderer.fontHeight) / 2;

            itemRenderer.renderGuiItemIcon(stack, iconPos.x(), iconPos.y());
            textRenderer.drawWithShadow(matrices, text(), textX, textY, color());
        }
    }
}
