package dev.bsprout.btweaks.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.rendertype.RenderType;

public class RoundRect {
    public static void draw(GuiGraphics graphics, float x, float y, float width, float height, int color) {
        graphics.fill((int)x, (int)y, (int)(x + width), (int)(y + height), color);
    } // TODO: KMS

    public static void drawText(GuiGraphics graphics, String text, float x, float y, float width, float height, int textColor) {
        var font = net.minecraft.client.Minecraft.getInstance().font;

        int textWidth = font.width(text);
        int centerX = (int) (x + (width / 2) - (textWidth / 2));
        int centerY = (int) (y + (height / 2) - (font.lineHeight / 2));

        graphics.drawString(font, text, centerX, centerY, textColor, false);
    }
}