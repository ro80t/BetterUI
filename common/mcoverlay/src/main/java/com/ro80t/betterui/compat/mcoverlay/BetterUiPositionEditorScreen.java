package com.ro80t.betterui.compat.mcoverlay;

import com.ro80t.betterui.BetterUiMod;
import com.ro80t.betterui.impl.config.HudLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
 * Vanilla-only, shared unchanged by every Forge and NeoForge version except
 * 1218, which uses {@code common:mcoverlay-v1218}'s own copy - see
 * {@link ArmorDurabilityOverlay} for why.
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
    private final List<Button> resetMenuButtons = new ArrayList<>();
    private Handle dragging;
    private boolean resizeMode;
    private boolean resetMenuOpen;
    private double dragGrabOffsetX;
    private double dragGrabOffsetY;
    private double resizeStartMouseX;
    private float resizeStartScale;

    public BetterUiPositionEditorScreen(final Screen parent) {
        super(Component.literal("BetterUI - Drag to Reposition"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final int backX = this.width - 4 - BACK_WIDTH;
        final int resizeX = backX - GAP - RESIZE_WIDTH;
        final int resetX = resizeX - GAP - RESET_WIDTH;
        final int menuX = resetX + RESET_WIDTH - MENU_WIDTH;

        this.addRenderableWidget(Button.builder(Component.literal("Back"), button -> this.onClose())
                .bounds(backX, TOP_Y, BACK_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(resizeLabel(), button -> {
                    this.resizeMode = !this.resizeMode;
                    button.setMessage(resizeLabel());
                })
                .bounds(resizeX, TOP_Y, RESIZE_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(resetToggleLabel(), button -> {
                    this.resetMenuOpen = !this.resetMenuOpen;
                    button.setMessage(resetToggleLabel());
                    setResetMenuVisible(this.resetMenuOpen);
                })
                .bounds(resetX, TOP_Y, RESET_WIDTH, BUTTON_HEIGHT)
                .build());

        int menuY = TOP_Y + BUTTON_HEIGHT + 4;
        addResetMenuItem(menuX, menuY, Component.literal("Reset FPS"),
                () -> BetterUiMod.getConfig().resetFpsLayout(), false);
        menuY += MENU_ROW_HEIGHT;
        addResetMenuItem(menuX, menuY, Component.literal("Reset Coordinates"),
                () -> BetterUiMod.getConfig().resetCoordinatesLayout(), false);
        menuY += MENU_ROW_HEIGHT;
        addResetMenuItem(menuX, menuY, Component.literal("Reset Armor HUD"),
                () -> BetterUiMod.getConfig().resetArmorHudLayout(), false);
        menuY += MENU_ROW_HEIGHT;

        final Button orientationButton = Button.builder(orientationLabel(), button -> {
                    BetterUiMod.getConfig().setArmorHudVertical(!BetterUiMod.getConfig().isArmorHudVertical());
                    BetterUiMod.saveConfig();
                    button.setMessage(orientationLabel());
                })
                .bounds(menuX, menuY, MENU_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(orientationButton);
        this.resetMenuButtons.add(orientationButton);
        menuY += MENU_ROW_HEIGHT;

        addResetMenuItem(menuX, menuY, Component.literal("Reset All"),
                () -> BetterUiMod.getConfig().resetHudLayouts(), true);

        setResetMenuVisible(false);
    }

    private void addResetMenuItem(final int x, final int y, final Component label, final Runnable resetAction,
                                   final boolean reopenScreen) {
        final Button button = Button.builder(label, b -> {
                    resetAction.run();
                    BetterUiMod.saveConfig();
                    if (reopenScreen) {
                        this.minecraft.setScreen(new BetterUiPositionEditorScreen(this.parent));
                    }
                })
                .bounds(x, y, MENU_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(button);
        this.resetMenuButtons.add(button);
    }

    private void setResetMenuVisible(final boolean visible) {
        for (final Button button : this.resetMenuButtons) {
            button.visible = visible;
            button.active = visible;
        }
    }

    private Component resizeLabel() {
        return Component.literal("Resize: " + (this.resizeMode ? "ON" : "OFF"));
    }

    private Component resetToggleLabel() {
        return Component.literal(this.resetMenuOpen ? "Reset ^" : "Reset v");
    }

    private static Component orientationLabel() {
        return Component.literal("Armor: " + (BetterUiMod.getConfig().isArmorHudVertical() ? "Vertical" : "Horizontal"));
    }

    @Override
    public void render(final GuiGraphics context, final int mouseX, final int mouseY, final float partialTick) {
        super.render(context, mouseX, mouseY, partialTick);

        this.handles.clear();

        context.drawString(this.font, this.resizeMode
                ? "Drag an element to resize it"
                : "Drag an element to move it", 4, 4, 0xFFFFFFFF);

        drawTextHandle(context, "FPS", "88 FPS", BetterUiMod.getConfig().getFpsLayout());
        drawTextHandle(context, "Coordinates", "X: 12.3 Y: 64.0 Z: -8.5", BetterUiMod.getConfig().getCoordinatesLayout());
        drawArmorHudPreview(context);

        if (this.dragging != null) {
            context.drawString(this.font, "Dragging: " + this.dragging.label(), 4, this.height - 16, 0xFFFFFF00);
        }
    }

    private void drawTextHandle(final GuiGraphics context, final String label, final String text, final HudLayout layout) {
        final int x = layout.x(this.width);
        final int y = layout.y(this.height);
        final int textWidth = Math.round(this.font.width(text) * layout.getScale());
        final int textHeight = Math.round(this.font.lineHeight * layout.getScale());

        context.fill(x - HANDLE_PADDING, y - HANDLE_PADDING, x + textWidth + HANDLE_PADDING,
                y + textHeight + HANDLE_PADDING, 0x556699FF);
        context.drawString(this.font, label + " (" + String.format("%.2f", layout.getScale()) + "x)",
                x - HANDLE_PADDING, y - HANDLE_PADDING - 10, 0xFFFFFF00);

        context.pose().pushPose();
        context.pose().translate(x, y, 0.0F);
        context.pose().scale(layout.getScale(), layout.getScale(), 1.0F);
        context.drawString(this.font, text, 0, 0, 0xFFFFFFFF);
        context.pose().popPose();

        this.handles.add(new Handle(label, layout, x - HANDLE_PADDING, y - HANDLE_PADDING,
                textWidth + HANDLE_PADDING * 2, textHeight + HANDLE_PADDING * 2));
    }

    private static final String[] ARMOR_SAMPLE_TEXTS = {"12/33", "456/789", "23/45", "1234/5678"};
    private static final Item[] ARMOR_SAMPLE_ITEMS = {
            Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET
    };

    private void drawArmorHudPreview(final GuiGraphics context) {
        final HudLayout layout = BetterUiMod.getConfig().getArmorHudLayout();
        final boolean vertical = BetterUiMod.getConfig().isArmorHudVertical();
        final int x = layout.x(this.width);
        final int y = layout.y(this.height);
        final int columnWidth = ArmorDurabilityOverlay.columnWidth(this.font);
        final int iconSize = ArmorDurabilityOverlay.ICON_SIZE;

        // Bounding box is derived from the exact same per-row positions used
        // below to draw the sample rows (and by ArmorDurabilityOverlay.render()
        // itself), so the highlighted preview area can never drift out of
        // sync with what's actually drawn - in either layout mode.
        int minX = 0;
        int maxX = iconSize;
        int minY = 0;
        int maxY = iconSize;
        for (int row = 0; row < ARMOR_SAMPLE_TEXTS.length; row++) {
            final ArmorDurabilityOverlay.Position pos = ArmorDurabilityOverlay.iconPosition(row, vertical, columnWidth);
            final int textWidth = this.font.width(ARMOR_SAMPLE_TEXTS[row]);
            minX = Math.min(minX, pos.x() - 4 - textWidth);
            maxX = Math.max(maxX, pos.x() + iconSize);
            minY = Math.min(minY, pos.y());
            maxY = Math.max(maxY, pos.y() + iconSize);
        }

        final int left = x + Math.round(minX * layout.getScale());
        final int right = x + Math.round(maxX * layout.getScale());
        final int top = y + Math.round(minY * layout.getScale());
        final int bottom = y + Math.round(maxY * layout.getScale());

        context.fill(left - HANDLE_PADDING, top - HANDLE_PADDING, right + HANDLE_PADDING,
                bottom + HANDLE_PADDING, 0x55FF9944);
        context.drawString(this.font, "Armor HUD (" + String.format("%.2f", layout.getScale()) + "x)",
                left - HANDLE_PADDING, top - HANDLE_PADDING - 10, 0xFFFFFF00);

        context.pose().pushPose();
        context.pose().translate(x, y, 0.0F);
        context.pose().scale(layout.getScale(), layout.getScale(), 1.0F);
        for (int row = 0; row < ARMOR_SAMPLE_TEXTS.length; row++) {
            final ArmorDurabilityOverlay.Position pos = ArmorDurabilityOverlay.iconPosition(row, vertical, columnWidth);
            final String text = ARMOR_SAMPLE_TEXTS[row];
            final int textWidth = this.font.width(text);
            context.renderItem(new ItemStack(ARMOR_SAMPLE_ITEMS[row]), pos.x(), pos.y());
            context.drawString(this.font, text, pos.x() - 4 - textWidth,
                    pos.y() + (iconSize - this.font.lineHeight) / 2, 0xFFFFFFFF);
        }
        context.pose().popPose();

        this.handles.add(new Handle("Armor HUD", layout, left - HANDLE_PADDING, top - HANDLE_PADDING,
                (right - left) + HANDLE_PADDING * 2, (bottom - top) + HANDLE_PADDING * 2));
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
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private record Handle(String label, HudLayout layout, int x, int y, int width, int height) {
    }
}
