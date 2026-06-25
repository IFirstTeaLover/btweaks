package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.PingTracker;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import static dev.bsprout.btweaks.client.widgets.FPS.Jersey;

public class Ping implements Widget {
    int frameSkip = 0;
    int cachedPing = 0;
    int targetSkip = mc.getFps();
    private boolean isEnabled;
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick, BRender bRender) {
        if (mc.getDebugOverlay().showDebugScreen()) return;

        if (!isEnabled) return;

        int color = WidgetGeneral.getGlobalWidgetColor();

        frameSkip++;

        cachedPing = PingTracker.getLivePing();

        String text = "Ping: " + cachedPing;

        int height = 15;
        int padding = 6;
        int fontSize = 10;
        float textWidth = Jersey.textSize(text, fontSize);

        float rectWidth = textWidth + padding * 2;
        bRender.roundRect((int) x, (int) y, (int) rectWidth, height, color, uiScale, 1);
        bRender.drawText(Jersey, text, x + padding, y + fontSize, fontSize, 0xFFFFFFFF, 4);
    }

    @Override
    public float getWidth(int uiScale) {
        if (!isEnabled) return 0;
        int padding = 6;
        return Jersey.textSize("Ping: " + cachedPing, 10) + (padding * 2);
    }

    @Override
    public float getHeight(int uiScale) {
        if (!isEnabled) return 0;
        return 15;
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
