package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.config.ConfigWindow;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import static dev.bsprout.btweaks.client.widgets.FPS.Jersey;

public class Coordinates implements Widget {
    private boolean isEnabled;
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick, BRender bRender) {
        if (mc.getDebugOverlay().showDebugScreen()) return;

        if (!isEnabled) return;

        int color = WidgetGeneral.getGlobalWidgetColor();

        String playerX = "X: " + Math.round(mc.player.getX());
        String playerY = "Y: " + Math.round(mc.player.getY());
        String playerZ = "Z: " + Math.round(mc.player.getZ());

        int height = 15;
        int padding = 6;

        float fontSize = (float) (height/1.5);

        float currentY = y;

        // Draw X
        float rectWidthX = Jersey.textSize(playerX, fontSize) + padding * 2;
        bRender.roundRect((int) x, (int) currentY, (int) rectWidthX, height, color, uiScale, 1);
        bRender.drawText(Jersey, playerX, x + padding, currentY + fontSize,  fontSize, 0xFFFF5555, 4);

        currentY += height + 3;

        // Draw Y
        float rectWidthY = Jersey.textSize(playerY, fontSize) + padding * 2;
        bRender.roundRect((int) x, (int) currentY, (int) rectWidthY, height, color, uiScale, 1);
        bRender.drawText(Jersey, playerY, x + padding, currentY + fontSize,  fontSize, 0xFF55FF55, 4);

        currentY += height + 3;

        // Draw Z
        float rectWidthZ = Jersey.textSize(playerZ, fontSize) + padding * 2;
        bRender.roundRect((int) x, (int) currentY, (int) rectWidthZ, height, color, uiScale, 1);
        bRender.drawText(Jersey, playerZ, x + padding, currentY + fontSize,  fontSize, 0xFF55FFFF, 4);
    }

    @Override
    public float getWidth(int uiScale) {
        if (mc.player == null) return 0;
        if (!isEnabled) return 0;
        String playerX = "X: " + Math.round(mc.player.getX());
        String playerY = "Y: " + Math.round(mc.player.getY());
        String playerZ = "Z: " + Math.round(mc.player.getZ());
        int padding = 6;
        int height = 15;
        float fontSize = (float) (height/1.5);
        float maxText = Math.max(
                Jersey.textSize(playerX, fontSize),
                Math.max(Jersey.textSize(playerY, fontSize), Jersey.textSize(playerZ, fontSize))
        );
        return maxText + padding * 2;
    }

    @Override
    public float getHeight(int uiScale) {
        if (!isEnabled) return 0;
        return 15 * 3 + 3 * 2; // 3 rows + 2 3px gaps
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
        return "Coordinates";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}