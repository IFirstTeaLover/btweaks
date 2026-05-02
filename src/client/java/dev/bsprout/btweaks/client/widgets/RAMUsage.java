package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class RAMUsage implements Widget{
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        if (mc.getDebugOverlay().showDebugScreen()) return;
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        // Convert bytes to Megabytes (1024 * 1024)
        long maxMB = maxMemory / 1048576L;
        long usedMB  = usedMemory / 1048576L;
        long allocMB = totalMemory / 1048576L;

        String line1 = "Memory used: " + usedMB + "MB";
        String line2 = "Memory allocated: " + allocMB + "/" + maxMB + "MB";

        int height = uiScale * 5;
        int padding = uiScale * 2;

        float currentY = y;

        // Draw 1
        float rectWidthX = mc.font.width(line1) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthX, height, 0x80262626);
        RoundRect.drawText(ctx, line1, x, currentY, rectWidthX, height, 0xFFFFFFFF);

        currentY += height;

        // Draw 2
        float rectWidthY = mc.font.width(line2) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthY, height, 0x80262626);
        RoundRect.drawText(ctx, line2, x, currentY, rectWidthY, height, 0xFFFFFFFF);
    }

    @Override
    public float getWidth(int uiScale) {
        Runtime runtime = Runtime.getRuntime();
        long usedMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        long maxMB  = runtime.maxMemory() / 1048576L;
        String line1 = "Memory used: " + usedMB + "MB";
        String line2 = "Memory allocated: " + usedMB + "/" + maxMB + "MB";
        int padding = uiScale * 2;
        return Math.max(mc.font.width(line1), mc.font.width(line2)) + padding * 2;
    }

    @Override
    public float getHeight(int uiScale) {
        return (uiScale * 5) * 2; // 2 rows
    }
}
