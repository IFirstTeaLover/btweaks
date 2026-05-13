package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import io.github.humbleui.skija.Canvas;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class FPS implements Widget {
    private boolean isEnabled;
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        if (mc.getDebugOverlay().showDebugScreen()) return;
        Canvas canvas = BtweaksClient.canvas;

        if (!isEnabled) return;

        canvas.save();
        canvas.translate(x, y);

        String text = "FPS: " + mc.getFps();
        int color = WidgetGeneral.getGlobalWidgetColor();

        int height = uiScale * 5;
        int padding = uiScale * 2;
        int textWidth = mc.font.width(text);
        float rectWidth = textWidth + padding * 2;
        RoundRect.draw(canvas, 0, 0, rectWidth, height, color, uiScale);
        RoundRect.drawText(canvas, text, 0, 0, rectWidth, height, 0xFFFFFFFF, "gsans", "left", 11f);

        canvas.restore();
    }

    @Override
    public float getWidth(int uiScale) {
        if (!isEnabled) return 0;
        return mc.font.width("FPS: " + mc.getFps()) + uiScale * 4;
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
        return "Framerate display";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}
