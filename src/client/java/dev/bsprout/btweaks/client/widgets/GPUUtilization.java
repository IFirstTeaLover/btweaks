package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class GPUUtilization implements Widget {
    int frameSkip = 0;
    double cachedUsage = 0;
    int targetSkip = 0;
    private boolean isEnabled;

    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick, BRender bRender) {
        if (mc.getDebugOverlay().showDebugScreen()) return;

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
        bRender.roundRect((int) x, (int) y, (int) rectWidth, height, color, uiScale);
        RoundRect.drawText(ctx, text, x, y, rectWidth, height, 0xFFFFFFFF);
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
