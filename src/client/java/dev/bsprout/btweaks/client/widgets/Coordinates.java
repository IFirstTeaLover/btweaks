package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class Coordinates implements Widget {
    private boolean isEnabled;

    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        if (BtweaksClient.canvas == null || mc.getDebugOverlay().showDebugScreen()) return;
        if (!isEnabled()) return;

        int bgColor = WidgetGeneral.getGlobalWidgetColor();

        String playerX = "X: " + Math.round(mc.player.getX());
        String playerY = "Y: " + Math.round(mc.player.getY());
        String playerZ = "Z: " + Math.round(mc.player.getZ());

        float rowHeight = uiScale * 10f;
        float padding = uiScale * 2f;
        float spacing = uiScale * 2f;
        float fontSize = uiScale * 8f;

        float currentY = y;

        // Draw rows
        drawCoordRow(playerX, x, currentY, rowHeight, padding, bgColor, uiScale, fontSize, 0xFFFF5555); // Red X
        currentY += rowHeight + spacing;

        drawCoordRow(playerY, x, currentY, rowHeight, padding, bgColor, uiScale, fontSize, 0xFF55FF55); // Green Y
        currentY += rowHeight + spacing;

        drawCoordRow(playerZ, x, currentY, rowHeight, padding, bgColor, uiScale, fontSize, 0xFF5555FF); // Blue Z
    }

    private void drawCoordRow(String text, float x, float y, float h, float pad, int bg, int scale, float fontSize, int labelColor) {
        float textWidth = mc.font.width(text);
        float rectW = textWidth + (pad * 2);

        RoundRect.draw(BtweaksClient.canvas, x, y, rectW, h, bg, scale);

        RoundRect.drawText(BtweaksClient.canvas, text, x + pad, y, textWidth, h, labelColor, "gsans", "left", fontSize);
    }

    @Override
    public float getWidth(int uiScale) {
        if (mc.player == null || !isEnabled()) return 0;

        String x = "X: " + Math.round(mc.player.getX());
        String y = "Y: " + Math.round(mc.player.getY());
        String z = "Z: " + Math.round(mc.player.getZ());

        float maxText = Math.max(mc.font.width(x), Math.max(mc.font.width(y), mc.font.width(z)));
        return maxText + (uiScale * 4); // text + padding
    }

    @Override
    public float getHeight(int uiScale) {
        if (!isEnabled()) return 0;
        float rowHeight = uiScale * 10f;
        float spacing = uiScale * 2f;
        return (rowHeight * 3) + (spacing * 2);
    }

    @Override
    public boolean isEnabled() {
        return ConfigManager.getBoolean(this.getName(), true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        ConfigManager.set(this.getName(), enabled);
        this.isEnabled = enabled;
    }

    @Override
    public String getName() { return "Coordinates"; }

    @Override public void setX(int x) {}
    @Override public void setY(int y) {}
}