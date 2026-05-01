package dev.bsprout.btweaks.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

import java.util.ArrayList;
import java.util.List;

public class WidgetRenderer implements HudRenderCallback {

    private final List<WidgetInstance> widgets = new ArrayList<>();

    public void add(Widget widget, float x, float y, WidgetInstance.Anchor anchor) {
        widgets.add(new WidgetInstance(widget, x, y, anchor));
    }

    @Override
    public void onHudRender(GuiGraphics ctx, DeltaTracker tick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.screen != null) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int uiScale = mc.getWindow().getGuiScale();

        for (WidgetInstance inst : widgets) {
            inst.widget.render(ctx, sw, sh, uiScale, inst.resolvedX(sw), inst.resolvedY(sh), tick);
        }
    }
}