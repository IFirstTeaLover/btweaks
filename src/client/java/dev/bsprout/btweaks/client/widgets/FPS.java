package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class FPS implements Widget {
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        if (mc.getDebugOverlay().showDebugScreen()) return;
        String text = "FPS: " + mc.getFps();

        int height = uiScale * 5;
        int padding = uiScale * 2;
        int textWidth = mc.font.width(text);
        float rectWidth = textWidth + padding * 2;
        RoundRect.draw(ctx, x, y, rectWidth, height, 0x80262626, uiScale);
        RoundRect.drawText(ctx, text, x, y, rectWidth, height, 0xFFFFFFFF);
    }

    @Override
    public float getWidth(int uiScale) {
        return mc.font.width("FPS: " + mc.getFps()) + uiScale * 4;
    }

    @Override
    public float getHeight(int uiScale) {
        return uiScale * 5;
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
        return "Framerate display";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}
