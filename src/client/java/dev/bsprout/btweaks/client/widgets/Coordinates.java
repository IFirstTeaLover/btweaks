package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class Coordinates implements Widget {
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        if (mc.getDebugOverlay().showDebugScreen()) return;
        String playerX = "§cX: §f" + Math.round(mc.player.getX());
        String playerY = "§aY: §f" + Math.round(mc.player.getY());
        String playerZ = "§bZ: §f" + Math.round(mc.player.getZ());

        int height = uiScale * 5;
        int padding = uiScale * 2;

        float currentY = y;

        // Draw X
        float rectWidthX = mc.font.width(playerX) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthX, height, 0x80262626, uiScale);
        RoundRect.drawText(ctx, playerX, x, currentY, rectWidthX, height, 0xFFFFFFFF);

        currentY += height + uiScale;

        // Draw Y
        float rectWidthY = mc.font.width(playerY) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthY, height, 0x80262626, uiScale);
        RoundRect.drawText(ctx, playerY, x, currentY, rectWidthY, height, 0xFFFFFFFF);

        currentY += height + uiScale;

        // Draw Z
        float rectWidthZ = mc.font.width(playerZ) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthZ, height, 0x80262626, uiScale);
        RoundRect.drawText(ctx, playerZ, x, currentY, rectWidthZ, height, 0xFFFFFFFF);
    }

    @Override
    public float getWidth(int uiScale) {
        if (mc.player == null) return 0;
        String playerX = "§cX: §f" + Math.round(mc.player.getX());
        String playerY = "§aY: §f" + Math.round(mc.player.getY());
        String playerZ = "§bZ: §f" + Math.round(mc.player.getZ());
        int padding = uiScale * 2;
        float maxText = Math.max(mc.font.width(playerX), Math.max(mc.font.width(playerY), mc.font.width(playerZ)));
        return maxText + padding * 2;
    }

    @Override
    public float getHeight(int uiScale) {
        return (uiScale * 5) * 3; // 3 rows
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean state) {

    }

    @Override
    public String getName() {
        return "Coordinates";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}