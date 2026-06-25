package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.brapi.client.BFont;
import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.AssetManager.getJersey;
import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class RAMUsage implements Widget{
    private boolean isEnabled;
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick, BRender bRender) {
        if (mc.getDebugOverlay().showDebugScreen()) return;

        if (!isEnabled) return;
        int color = WidgetGeneral.getGlobalWidgetColor();
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        // Convert bytes to Megabytes (1024 * 1024)
        long maxMB = maxMemory / 1048576L;
        long usedMB  = usedMemory / 1048576L;
        long allocMB = totalMemory / 1048576L;

        String line1 = "Memory: " + usedMB;
        String line2 = "Allocated: " + allocMB + "/" + maxMB;

        int height = uiScale * 5;
        int padding = uiScale * 2;

        BFont Jersey = getJersey();

        float currentY = y;
        int fontSize = 10;

        // Draw 1
        float rectWidthX = mc.font.width(line1) + padding * 2;
        bRender.roundRect((int)x, (int) currentY, (int) rectWidthX, height, color, 3, 1);
        bRender.drawText(Jersey, line1, x + padding, currentY + fontSize, fontSize, 0xFFFFFFFF, 4);

        currentY += height + uiScale;

        // Draw 2
        float rectWidthY = mc.font.width(line2) + padding * 2;
        bRender.roundRect((int) x, (int) currentY, (int) rectWidthY, height, color, 3, 1);
        bRender.drawText(Jersey, line2, x + padding, currentY + fontSize, fontSize, 0xFFFFFFFF, 4);
    }


    @Override
    public float getWidth(int uiScale) {
        if (!isEnabled) return 0;
        Runtime runtime = Runtime.getRuntime();
        long usedMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        long maxMB  = runtime.maxMemory() / 1048576L;
        String line1 = "Memory: " + usedMB;
        String line2 = "Allocated: " + usedMB + "/" + maxMB;
        int padding = uiScale * 2;
        return Math.max(getJersey().textSize(line1, 10), getJersey().textSize(line2, 10)) + padding * 2;
    }

    @Override
    public float getHeight(int uiScale) {
        if (!isEnabled) return 0;
        return (uiScale * 5) * 2; // 2 rows
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
        return "RAM Usage";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}
