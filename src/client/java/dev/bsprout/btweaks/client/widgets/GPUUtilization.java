package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import io.github.humbleui.skija.Canvas;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class GPUUtilization implements Widget {
    int frameSkip = 0;
    double cachedUsage = 0;
    int targetSkip = 0;
    private boolean isEnabled;

    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        if (mc.getDebugOverlay().showDebugScreen()) return;
        Canvas canvas = BtweaksClient.canvas;

        if (!isEnabled) return;

        int color = WidgetGeneral.getGlobalWidgetColor();

        frameSkip++;

        if (frameSkip >= targetSkip / 5) {
            targetSkip = mc.getFps();
            frameSkip = 0;
            cachedUsage = mc.getGpuUtilization();
        }

        String text = "GPU: " + cachedUsage;

        int height = uiScale * 5;
        int padding = uiScale * 2;
        int textWidth = mc.font.width(text);
        float rectWidth = textWidth + padding * 2;
        RoundRect.draw(canvas, x, y, rectWidth, height, color, uiScale);
        RoundRect.drawText(canvas, text, x, y, rectWidth, height, 0xFFFFFFFF, "gsans", "left", 11f);
    }

    @Override
    public float getWidth(int uiScale) {
        if (!isEnabled) return 0;
        int padding = uiScale * 2;
        return mc.font.width("GPU: " + cachedUsage) + (padding * 2);
    }

    @Override
    public float getHeight(int uiScale) {
        if (!isEnabled) return 0;
        return uiScale * 5;
    }

    @Override
    public boolean isEnabled() {
        boolean enabled = ConfigManager.getBoolean(this.getName(), true);
        isEnabled = enabled;
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        ConfigManager.set(this.getName(), enabled);
        isEnabled = enabled;
    }

    @Override
    public String getName() {
        return "GPU Utilization display";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}
