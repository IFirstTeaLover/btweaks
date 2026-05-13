package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import io.github.humbleui.skija.Canvas;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import dev.bsprout.btweaks.client.BtweaksClient;

public class Ping implements Widget {
    int frameSkip = 0;
    int cachedPing = 0;
    int targetSkip = mc.getFps();
    private boolean isEnabled;
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        Canvas canvas = BtweaksClient.canvas;
        if (mc.getDebugOverlay().showDebugScreen()) return;

        if (!isEnabled) return;

        int color = WidgetGeneral.getGlobalWidgetColor();

        frameSkip++;

        if (frameSkip >= targetSkip) {
            targetSkip = mc.getFps();
            frameSkip = 0;
            if (mc.getConnection() != null && mc.player != null) {
                var entry = mc.getConnection().getPlayerInfo(mc.player.getUUID());
                if (entry != null) {
                    cachedPing = entry.getLatency();
                }
            }
        }

        String text = "Ping: " + cachedPing;

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
        return mc.font.width("Ping: " + cachedPing) + (padding * 2);
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
        return "Ping display";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}
