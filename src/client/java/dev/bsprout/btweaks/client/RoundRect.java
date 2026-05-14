package dev.bsprout.btweaks.client;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class RoundRect {
    public static Typeface gsansTypeface;
    public static Typeface interTypeface;

    public static void draw(Canvas canvas, float x, float y, float width, float height, int argb,
                            float tl, float tr, float bl, float br) {

        if (width <= 0 || height <= 0) return;

        try (Paint paint = new Paint().setColor(argb).setAntiAlias(true)) {
            RRect rrect = RRect.makeComplexLTRB(
                    x, y, x + width, y + height,
                    new float[] { tl, tl, tr, tr, br, br, bl, bl }
            );
            canvas.drawRRect(rrect, paint);
        }
    }

    public static void draw(Canvas canvas, float x, float y, float width, float height, int argb, float radius) {
        draw(canvas, x, y, width, height, argb, radius, radius, radius, radius);
    }

    public static void drawText(Canvas canvas, String text, float x, float y, float width, float height,
                                int textColor, String fontType, String align, float fontSize) {

        Typeface face = (fontType.equals("gsans")) ? gsansTypeface : interTypeface;

        String safeText = text.replace('\uFFFD', ' ')
                .replaceAll("[^\u0000-\uFFFF]", "?");

        try (Font font = new Font(face, fontSize);
             Paint paint = new Paint().setColor(textColor).setAntiAlias(true)) {

            // Measure text for alignment
            Rect bounds = font.measureText(text, paint);
            float textWidth = bounds.getWidth();
            float textHeight = font.getMetrics().getCapHeight(); // Better for vertical centering

            float drawX = x;
            float drawY = y + (height / 2) + (textHeight / 2); // Vertical center

            if (align.equals("center")) {
                drawX = x + (width / 2) - (textWidth / 2);
            } else if (align.equals("right")) {
                drawX = x + width - textWidth;
            }

            canvas.drawString(safeText, drawX, drawY, font, paint);
        }
    }

    /**
     * Image rendering using Skija.
     * Note: You will need to convert your Minecraft Identifier to a Skija Image once.
     */
    public static void drawImage(Canvas canvas, Image skijaImage, float x, float y, float width, float height) {
        if (skijaImage == null) return;

        canvas.drawImageRect(skijaImage, Rect.makeXYWH(x, y, width, height));
    }
}