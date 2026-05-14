package dev.bsprout.btweaks.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

import java.util.ArrayList;
import java.util.List;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class WidgetRenderer implements HudRenderCallback {

    private final List<WidgetInstance> widgets = new ArrayList<>();

    public void add(Widget widget, int col, int row, WidgetInstance.Anchor anchor) {
        widgets.add(new WidgetInstance(widget, col, row, anchor));
    }

    @Override
    public void onHudRender(GuiGraphics ctx, DeltaTracker tick) {
        final float GAP = mc.getWindow().getGuiScale();
        if (mc.player == null || mc.options.hideGui) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int uiScale = mc.getWindow().getGuiScale();

        for (WidgetInstance.Anchor anchor : WidgetInstance.Anchor.values()) {
            List<WidgetInstance> group = widgets.stream()
                    .filter(w -> w.anchor == anchor)
                    .toList();
            if (group.isEmpty()) continue;

            List<WidgetInstance> auto   = group.stream().filter(w -> w.col == -1).toList();
            List<WidgetInstance> manual = group.stream().filter(w -> w.col != -1).toList();

            // auto widgets
            float curY = 0;
            float maxW = 0;
            List<float[]> autoPositions = new ArrayList<>();
            for (WidgetInstance inst : auto) {
                float w = inst.widget.getWidth(uiScale);
                float h = inst.widget.getHeight(uiScale);
                autoPositions.add(new float[]{0, curY});
                if (h > 0) curY += h + GAP;
                maxW = Math.max(maxW, w);
            }
            float totalAutoH = curY - GAP;

            float autoOriginX = switch (anchor) {
                case TOP_LEFT,    BOTTOM_LEFT  -> GAP;
                case TOP_RIGHT,   BOTTOM_RIGHT -> sw - maxW - GAP;
                case CENTER                    -> sw / 2f - maxW / 2f;
            };
            float autoOriginY = switch (anchor) {
                case TOP_LEFT,    TOP_RIGHT    -> GAP;
                case BOTTOM_LEFT, BOTTOM_RIGHT -> sh - totalAutoH - GAP;
                case CENTER                    -> sh / 2f - totalAutoH / 2f;
            };

            for (int i = 0; i < auto.size(); i++) {
                float[] pos = autoPositions.get(i);
                WidgetInstance inst = auto.get(i);
                float wx = autoOriginX + pos[0];
                float wy = autoOriginY + pos[1];
                // bottom anchors: pass bottom edge so widget builds upward
                boolean bottomAnchor = anchor == WidgetInstance.Anchor.BOTTOM_LEFT || anchor == WidgetInstance.Anchor.BOTTOM_RIGHT;
                inst.widget.render(ctx, uiScale, wx, bottomAnchor ? wy + inst.widget.getHeight(uiScale) : wy, tick);
            }

            // manual widgets
            for (WidgetInstance inst : manual) {
                float w = inst.widget.getWidth(uiScale);
                float h = inst.widget.getHeight(uiScale);

                float wx = switch (anchor) {
                    case TOP_LEFT,    BOTTOM_LEFT  -> inst.col;
                    case TOP_RIGHT,   BOTTOM_RIGHT -> sw - w - inst.col;
                    case CENTER                    -> sw / 2f - w / 2f + inst.col;
                };
                float wy = switch (anchor) {
                    case TOP_LEFT,    TOP_RIGHT    -> inst.row;
                    case BOTTOM_LEFT, BOTTOM_RIGHT -> sh - h - inst.row;
                    case CENTER                    -> sh / 2f - h / 2f + inst.row;
                };
                boolean bottomAnchor = anchor == WidgetInstance.Anchor.BOTTOM_LEFT || anchor == WidgetInstance.Anchor.BOTTOM_RIGHT;
                inst.widget.render(ctx, uiScale, wx, bottomAnchor ? wy + h : wy, tick);
            }
        }
    }

    public List<WidgetInstance> getWidgets() {
        return widgets;
    }
}