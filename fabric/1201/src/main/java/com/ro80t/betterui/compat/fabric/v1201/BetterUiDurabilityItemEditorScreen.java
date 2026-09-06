package com.ro80t.betterui.compat.fabric.v1201;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Lets the player drag the per-item-slot durability number to a new offset
 * and, in resize mode, scale it, with a live preview centered on a sample
 * item slot. Opened from the main {@link BetterUiSettingsScreen}.
 * <p>
 * Kept separate from {@link BetterUiPositionEditorScreen} because this
 * element isn't drawn at one fixed screen position - it's a per-slot offset,
 * not an anchor+offset like the other HUD elements.
 */
public final class BetterUiDurabilityItemEditorScreen extends Screen {
    private static final int HANDLE_PADDING = 3;
    private static final int TOP_Y = 4;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BACK_WIDTH = 50;
    private static final int RESIZE_WIDTH = 70;
    private static final int RESET_WIDTH = 60;
    private static final int GAP = 4;

    private final Screen parent;
    private boolean resizeMode;
    private boolean dragging;
    private int handleX;
    private int handleY;
    private int handleWidth;
    private int handleHeight;
    private double dragGrabOffsetX;
    private double dragGrabOffsetY;
    private double resizeStartMouseX;
    private float resizeStartScale;

    public BetterUiDurabilityItemEditorScreen(final Screen parent) {
        super(Text.literal("BetterUI - Item Durability Number"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final int backX = this.width - 4 - BACK_WIDTH;
        final int resizeX = backX - GAP - RESIZE_WIDTH;
        final int resetX = resizeX - GAP - RESET_WIDTH;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.close())
                .dimensions(backX, TOP_Y, BACK_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addDrawableChild(ButtonWidget.builder(resizeLabel(), button -> {
                    this.resizeMode = !this.resizeMode;
                    button.setMessage(resizeLabel());
                })
                .dimensions(resizeX, TOP_Y, RESIZE_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), button -> {
                    BetterUiMod.getConfig().resetDurabilityItemLayout();
                    BetterUiMod.saveConfig();
                })
                .dimensions(resetX, TOP_Y, RESET_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    private Text resizeLabel() {
        return Text.literal("Resize: " + (this.resizeMode ? "ON" : "OFF"));
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float partialTick) {
        super.render(context, mouseX, mouseY, partialTick);

        context.drawText(this.textRenderer, this.resizeMode
                ? "Drag the number to resize it"
                : "Drag the number to move it", 4, 4, 0xFFFFFFFF, false);

        drawPreview(context);
    }

    private void drawPreview(final DrawContext context) {
        final HudLayout layout = BetterUiMod.getConfig().getDurabilityItemLayout();
        final int slotX = this.width / 2 - 8;
        final int slotY = this.height / 2 - 8;

        context.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0x55FFFFFF);
        context.drawItem(new ItemStack(Items.DIAMOND_PICKAXE), slotX, slotY);

        final String text = "12";
        final int textWidth = this.textRenderer.getWidth(text);
        final int x = slotX + 8 + layout.getOffsetX();
        final int y = slotY + 8 + layout.getOffsetY();

        context.drawText(this.textRenderer, "Item Number (" + String.format("%.2f", layout.getScale()) + "x)",
                x - 40, y - 24, 0xFFFF00, false);

        final MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 200.0F);
        matrices.scale(layout.getScale(), layout.getScale(), 1.0F);
        context.drawText(this.textRenderer, text, -textWidth / 2, 0, 0xFFFFFFFF, false);
        matrices.pop();

        final int handleSize = Math.round(Math.max(this.textRenderer.fontHeight, textWidth) * layout.getScale())
                + HANDLE_PADDING * 2;
        this.handleX = x - handleSize / 2;
        this.handleY = y - handleSize / 2;
        this.handleWidth = handleSize;
        this.handleHeight = handleSize;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (button != 0) {
            return false;
        }

        if (mouseX >= this.handleX && mouseX <= this.handleX + this.handleWidth
                && mouseY >= this.handleY && mouseY <= this.handleY + this.handleHeight) {
            this.dragging = true;
            final HudLayout layout = BetterUiMod.getConfig().getDurabilityItemLayout();
            if (this.resizeMode) {
                this.resizeStartMouseX = mouseX;
                this.resizeStartScale = layout.getScale();
            } else {
                final int slotX = this.width / 2 - 8;
                final int slotY = this.height / 2 - 8;
                this.dragGrabOffsetX = mouseX - (slotX + 8 + layout.getOffsetX());
                this.dragGrabOffsetY = mouseY - (slotY + 8 + layout.getOffsetY());
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button,
                                 final double dragX, final double dragY) {
        if (this.dragging) {
            final HudLayout layout = BetterUiMod.getConfig().getDurabilityItemLayout();
            if (this.resizeMode) {
                final float newScale = this.resizeStartScale + (float) (mouseX - this.resizeStartMouseX) / 100.0F;
                layout.setScale(Math.max(0.25F, Math.min(4.0F, newScale)));
            } else {
                final int slotX = this.width / 2 - 8;
                final int slotY = this.height / 2 - 8;
                layout.setOffsetX((int) Math.round(mouseX - this.dragGrabOffsetX) - (slotX + 8));
                layout.setOffsetY((int) Math.round(mouseY - this.dragGrabOffsetY) - (slotY + 8));
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (this.dragging) {
            this.dragging = false;
            BetterUiMod.saveConfig();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
