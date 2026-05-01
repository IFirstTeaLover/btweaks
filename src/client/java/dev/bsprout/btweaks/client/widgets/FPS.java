package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class FPS implements Widget {
    @Override
    public void render(GuiGraphics ctx, int sw, int sh, int uiScale, float x, float y, DeltaTracker tick) {
        String text = "FPS: " + mc.getFps();

        int height = uiScale * 5;
        int padding = uiScale * 2;
        int textWidth = mc.font.width(text);
        float rectWidth = textWidth + padding * 2;
        RoundRect.draw(ctx, x, y, rectWidth, height, 0x80262626);
        RoundRect.drawText(ctx, text, x, y, rectWidth, height, 0xFFFFFFFF);
    }
}
