package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability, at a position/scale editable via the BetterUI
 * position editor screen ({@code armorHudLayout} in the config), stacked
 * either vertically or horizontally depending on {@code armorHudVertical}.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * Byte-identical to {@code fabric:common-drawcontext}'s copy, but compiled
 * separately against 1218 mappings because {@code DrawContext.drawTextWithShadow(...)}
 * changed its return type from {@code int} to {@code void} in that version,
 * which is a binary-incompatible change: a class compiled against the older
 * signature throws {@code NoSuchMethodError} when run against the newer one,
 * and vice versa. Used only by the 1218 Fabric module.
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
     * see {@code BetterUiPositionEditorScreen#drawArmorHudPreview}.
     */
    static int columnWidth(final TextRenderer textRenderer) {
        return ICON_SIZE + COLUMN_GAP + textRenderer.getWidth(WIDTH_SAMPLE_TEXT) + COLUMN_GAP;
    }

    public static void render(final DrawContext context) {
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

        final int columnWidth = columnWidth(client.textRenderer);

        final Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(layout.x(context.getScaledWindowWidth()), layout.y(context.getScaledWindowHeight()));
        matrices.scale(layout.getScale());

        int row = 0;
        for (final EquipmentSlot slot : ARMOR_SLOTS_BOTTOM_UP) {
            final DurabilityEntry entry = new DurabilityEntry(player.getEquippedStack(slot));
            if (!entry.isVisible()) {
                continue;
            }

            entry.draw(context, client.textRenderer, iconPosition(row, vertical, columnWidth));
            row++;
        }

        matrices.popMatrix();
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

        void draw(final DrawContext context, final TextRenderer textRenderer, final Position iconPos) {
            final int textX = iconPos.x() - 4 - textRenderer.getWidth(text());
            final int textY = iconPos.y() + (ICON_SIZE - textRenderer.fontHeight) / 2;

            context.drawItem(stack, iconPos.x(), iconPos.y());
            context.drawTextWithShadow(textRenderer, text(), textX, textY, color());
        }
    }
}
