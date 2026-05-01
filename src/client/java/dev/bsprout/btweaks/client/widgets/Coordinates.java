package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class Coordinates implements Widget {
    @Override
    public void render(GuiGraphics ctx, int sw, int sh, int uiScale, float x, float y, DeltaTracker tick) {
        String playerX = "§cX: §f" + Math.round(mc.player.getX());
        String playerY = "§aY: §f" + Math.round(mc.player.getY());
        String playerZ = "§bZ: §f" + Math.round(mc.player.getZ());

        int height = uiScale * 5;
        int padding = uiScale * 2;

        float currentY = y + uiScale + height;

        // Draw X
        float rectWidthX = mc.font.width(playerX) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthX, height, 0x80262626);
        RoundRect.drawText(ctx, playerX, x, currentY, rectWidthX, height, 0xFFFFFFFF);

        currentY += height;

        // Draw Y
        float rectWidthY = mc.font.width(playerY) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthY, height, 0x80262626);
        RoundRect.drawText(ctx, playerY, x, currentY, rectWidthY, height, 0xFFFFFFFF);

        currentY += height;

        // Draw Z
        float rectWidthZ = mc.font.width(playerZ) + padding * 2;
        RoundRect.draw(ctx, x, currentY, rectWidthZ, height, 0x80262626);
        RoundRect.drawText(ctx, playerZ, x, currentY, rectWidthZ, height, 0xFFFFFFFF);
    }
}