package com.ro80t.betterui.compat.fabric.v1165;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * "Durability show"-style HUD overlay: lists the equipped armor pieces and
 * their remaining durability in the bottom-right corner of the screen.
 * Toggle with {@code durabilityHudEnabled} in the mod's config file.
 * <p>
 * 1.16.5 predates {@code Item.getItemBarColor}, so unlike the 1182/1192
 * shared renderer this one computes vanilla's own bar color by hand and
 * can't be shared with them.
 */
public final class DurabilityHud {
    private static final EquipmentSlot[] ARMOR_SLOTS_BOTTOM_UP = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    static final int MAX_ROWS = ARMOR_SLOTS_BOTTOM_UP.length;
    static final int ICON_SIZE = 16;
    static final int ROW_HEIGHT = 18;
    private static final int COLUMN_GAP = 4;
    private static final String WIDTH_SAMPLE_TEXT = "9999/9999";

    private DurabilityHud() {
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

    public static void register() {
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            render(matrices);
            FpsOverlay.render(matrices);
            CoordinatesOverlay.render(matrices);
        });
    }

    private static void render(final MatrixStack matrices) {
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
            final float durabilityFraction = Math.max(0.0F, (float) remaining() / stack.getMaxDamage());
            return MathHelper.hsvToRgb(durabilityFraction / 3.0F, 1.0F, 1.0F) | 0xFF000000;
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
