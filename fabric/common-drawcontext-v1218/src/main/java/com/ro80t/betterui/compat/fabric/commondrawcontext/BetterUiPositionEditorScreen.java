package com.ro80t.betterui.compat.fabric.commondrawcontext;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Lets the player drag BetterUI's HUD elements (FPS, coordinates, armor
 * durability HUD) to a new position, with a live preview. Opened via the
 * "Edit HUD Positions" button on the main {@link BetterUiSettingsScreen}.
 * <p>
 * Only three buttons live directly on screen (top-right): Back, a Resize
 * toggle (drag changes scale instead of position while it's on), and a
 * Reset menu toggle that reveals a list of per-element/orientation/all reset
 * buttons only while open. Everything else is done by dragging the preview.
 * <p>
 * The per-item durability number has its own dedicated editor,
 * {@link BetterUiDurabilityItemEditorScreen}, since it isn't drawn at one
 * fixed screen position like the elements here.
 * <p>
 * Byte-identical in intent to {@code fabric:common-drawcontext}'s copy, but
 * compiled separately against 1218 mappings and using {@link Matrix3x2fStack}
 * instead of {@code MatrixStack} - see {@link ArmorDurabilityOverlay} for
 * why. Used only by the 1218 Fabric module.
 */
public final class BetterUiPositionEditorScreen extends Screen {
    private static final int HANDLE_PADDING = 3;
    private static final int TOP_Y = 4;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BACK_WIDTH = 50;
    private static final int RESIZE_WIDTH = 70;
    private static final int RESET_WIDTH = 60;
    private static final int MENU_WIDTH = 160;
    private static final int MENU_ROW_HEIGHT = 24;
    private static final int GAP = 4;

    private final Screen parent;
    private final List<Handle> handles = new ArrayList<>();
    private final List<ButtonWidget> resetMenuButtons = new ArrayList<>();
    private Handle dragging;
    private boolean resizeMode;
    private boolean resetMenuOpen;
    private double dragGrabOffsetX;
    private double dragGrabOffsetY;
    private double resizeStartMouseX;
    private float resizeStartScale;

    public BetterUiPositionEditorScreen(final Screen parent) {
        super(Text.literal("BetterUI - Drag to Reposition"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final int backX = this.width - 4 - BACK_WIDTH;
        final int resizeX = backX - GAP - RESIZE_WIDTH;
        final int resetX = resizeX - GAP - RESET_WIDTH;
        final int menuX = resetX + RESET_WIDTH - MENU_WIDTH;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.close())
                .dimensions(backX, TOP_Y, BACK_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addDrawableChild(ButtonWidget.builder(resizeLabel(), button -> {
                    this.resizeMode = !this.resizeMode;
                    button.setMessage(resizeLabel());
                })
                .dimensions(resizeX, TOP_Y, RESIZE_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addDrawableChild(ButtonWidget.builder(resetToggleLabel(), button -> {
                    this.resetMenuOpen = !this.resetMenuOpen;
                    button.setMessage(resetToggleLabel());
                    setResetMenuVisible(this.resetMenuOpen);
                })
                .dimensions(resetX, TOP_Y, RESET_WIDTH, BUTTON_HEIGHT)
                .build());

        int menuY = TOP_Y + BUTTON_HEIGHT + 4;
        addResetMenuItem(menuX, menuY, Text.literal("Reset FPS"),
                () -> BetterUiMod.getConfig().resetFpsLayout(), false);
        menuY += MENU_ROW_HEIGHT;
        addResetMenuItem(menuX, menuY, Text.literal("Reset Coordinates"),
                () -> BetterUiMod.getConfig().resetCoordinatesLayout(), false);
        menuY += MENU_ROW_HEIGHT;
        addResetMenuItem(menuX, menuY, Text.literal("Reset Armor HUD"),
                () -> BetterUiMod.getConfig().resetArmorHudLayout(), false);
        menuY += MENU_ROW_HEIGHT;

        final ButtonWidget orientationButton = ButtonWidget.builder(orientationLabel(), button -> {
                    BetterUiMod.getConfig().setArmorHudVertical(!BetterUiMod.getConfig().isArmorHudVertical());
                    BetterUiMod.saveConfig();
                    button.setMessage(orientationLabel());
                })
                .dimensions(menuX, menuY, MENU_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addDrawableChild(orientationButton);
        this.resetMenuButtons.add(orientationButton);
        menuY += MENU_ROW_HEIGHT;

        addResetMenuItem(menuX, menuY, Text.literal("Reset All"),
                () -> BetterUiMod.getConfig().resetHudLayouts(), true);

        setResetMenuVisible(false);
    }

    private void addResetMenuItem(final int x, final int y, final Text label, final Runnable resetAction,
                                   final boolean reopenScreen) {
        final ButtonWidget button = ButtonWidget.builder(label, b -> {
                    resetAction.run();
                    BetterUiMod.saveConfig();
                    if (reopenScreen) {
                        this.client.setScreen(new BetterUiPositionEditorScreen(this.parent));
                    }
                })
                .dimensions(x, y, MENU_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addDrawableChild(button);
        this.resetMenuButtons.add(button);
    }

    private void setResetMenuVisible(final boolean visible) {
        for (final ButtonWidget button : this.resetMenuButtons) {
            button.visible = visible;
            button.active = visible;
        }
    }

    private Text resizeLabel() {
        return Text.literal("Resize: " + (this.resizeMode ? "ON" : "OFF"));
    }

    private Text resetToggleLabel() {
        return Text.literal(this.resetMenuOpen ? "Reset ^" : "Reset v");
    }

    private static Text orientationLabel() {
        return Text.literal("Armor: " + (BetterUiMod.getConfig().isArmorHudVertical() ? "Vertical" : "Horizontal"));
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float partialTick) {
        super.render(context, mouseX, mouseY, partialTick);

        this.handles.clear();

        context.drawText(this.textRenderer, this.resizeMode
                ? "Drag an element to resize it"
                : "Drag an element to move it", 4, 4, 0xFFFFFFFF, false);

        drawTextHandle(context, "FPS", "88 FPS", BetterUiMod.getConfig().getFpsLayout());
        drawTextHandle(context, "Coordinates", "X: 12.3 Y: 64.0 Z: -8.5", BetterUiMod.getConfig().getCoordinatesLayout());
        drawArmorHudPreview(context);

        if (this.dragging != null) {
            context.drawText(this.textRenderer, "Dragging: " + this.dragging.label(), 4, this.height - 16, 0xFFFF00, false);
        }
    }

    private void drawTextHandle(final DrawContext context, final String label, final String text, final HudLayout layout) {
        final int x = layout.x(this.width);
        final int y = layout.y(this.height);
        final int textWidth = Math.round(this.textRenderer.getWidth(text) * layout.getScale());
        final int textHeight = Math.round(this.textRenderer.fontHeight * layout.getScale());

        context.fill(x - HANDLE_PADDING, y - HANDLE_PADDING, x + textWidth + HANDLE_PADDING,
                y + textHeight + HANDLE_PADDING, 0x556699FF);
        context.drawText(this.textRenderer, label + " (" + String.format("%.2f", layout.getScale()) + "x)",
                x - HANDLE_PADDING, y - HANDLE_PADDING - 10, 0xFFFF00, false);

        final Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(layout.getScale());
        context.drawText(this.textRenderer, text, 0, 0, 0xFFFFFFFF, false);
        matrices.popMatrix();

        this.handles.add(new Handle(label, layout, x - HANDLE_PADDING, y - HANDLE_PADDING,
                textWidth + HANDLE_PADDING * 2, textHeight + HANDLE_PADDING * 2));
    }

    private void drawArmorHudPreview(final DrawContext context) {
        final HudLayout layout = BetterUiMod.getConfig().getArmorHudLayout();
        final boolean vertical = BetterUiMod.getConfig().isArmorHudVertical();
        final int x = layout.x(this.width);
        final int y = layout.y(this.height);
        final String text = "12/33";
        final int textWidth = this.textRenderer.getWidth(text);
        final int boxWidth = Math.round((16 + 4 + textWidth) * layout.getScale());
        final int boxHeight = Math.round(16 * layout.getScale());
        final int previewWidth = vertical ? boxWidth : boxWidth * 2 + 8;
        final int previewHeight = vertical ? boxHeight * 2 + 8 : boxHeight;
        final int left = vertical ? x : x - previewWidth + boxWidth;
        final int top = vertical ? y - previewHeight + boxHeight : y;

        context.fill(left - HANDLE_PADDING, top - HANDLE_PADDING, left + previewWidth + HANDLE_PADDING,
                top + previewHeight + HANDLE_PADDING, 0x55FF9944);
        context.drawText(this.textRenderer, "Armor HUD (" + String.format("%.2f", layout.getScale()) + "x)",
                left - HANDLE_PADDING, top - HANDLE_PADDING - 10, 0xFFFF00, false);

        final Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(layout.getScale());
        context.drawItem(new ItemStack(Items.DIAMOND_CHESTPLATE), 0, 0);
        context.drawText(this.textRenderer, text, -4 - textWidth, (16 - this.textRenderer.fontHeight) / 2, 0xFFFFFFFF, false);
        matrices.popMatrix();

        this.handles.add(new Handle("Armor HUD", layout, left - HANDLE_PADDING, top - HANDLE_PADDING,
                previewWidth + HANDLE_PADDING * 2, previewHeight + HANDLE_PADDING * 2));
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (button != 0) {
            return false;
        }

        for (final Handle handle : this.handles) {
            if (mouseX >= handle.x() && mouseX <= handle.x() + handle.width()
                    && mouseY >= handle.y() && mouseY <= handle.y() + handle.height()) {
                this.dragging = handle;
                if (this.resizeMode) {
                    this.resizeStartMouseX = mouseX;
                    this.resizeStartScale = handle.layout().getScale();
                } else {
                    this.dragGrabOffsetX = mouseX - handle.layout().x(this.width);
                    this.dragGrabOffsetY = mouseY - handle.layout().y(this.height);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button,
                                 final double dragX, final double dragY) {
        if (this.dragging != null) {
            final HudLayout layout = this.dragging.layout();
            if (this.resizeMode) {
                final float newScale = this.resizeStartScale + (float) (mouseX - this.resizeStartMouseX) / 100.0F;
                layout.setScale(Math.max(0.25F, Math.min(4.0F, newScale)));
            } else {
                final int anchorPixelX = Math.round(layout.getAnchorX() * this.width);
                final int anchorPixelY = Math.round(layout.getAnchorY() * this.height);
                layout.setOffsetX((int) Math.round(mouseX - this.dragGrabOffsetX) - anchorPixelX);
                layout.setOffsetY((int) Math.round(mouseY - this.dragGrabOffsetY) - anchorPixelY);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (this.dragging != null) {
            this.dragging = null;
            BetterUiMod.saveConfig();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private record Handle(String label, HudLayout layout, int x, int y, int width, int height) {
    }
}
