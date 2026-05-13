package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.KeyLogger;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.Tinter;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import io.github.humbleui.skija.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.components.ChatComponent;

import java.util.ArrayDeque;
import java.util.Deque;

public class Keystroke implements Widget {
    private boolean isEnabled;
    private float[] blend = {0, 0, 0, 0, 0, 0, 0};
    private static final float LERPSPEED = 0.3f;

    private final Deque<Long> lmbClicks = new ArrayDeque<>();
    private final Deque<Long> rmbClicks = new ArrayDeque<>();
    private boolean lastLmb = false;
    private boolean lastRmb = false;

    private int lerpColor(int from, int to, float t) {
        int a = (int) ((from >> 24 & 0xFF) + ((to >> 24 & 0xFF) - (from >> 24 & 0xFF)) * t);
        int r = (int) ((from >> 16 & 0xFF) + ((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
        int g = (int) ((from >> 8  & 0xFF) + ((to >> 8  & 0xFF) - (from >> 8  & 0xFF)) * t);
        int b = (int) ((from       & 0xFF) + ((to       & 0xFF) - (from       & 0xFF)) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        Canvas canvas = BtweaksClient.canvas;
        if (!isEnabled) return;
        Minecraft mc = Minecraft.getInstance();
        long now = System.currentTimeMillis();
        int sw = mc.getWindow().getGuiScaledWidth();

        float pad  = uiScale;
        float size = sw / (90.0f / uiScale);

        boolean lmb = KeyLogger.isAttackPressed;
        boolean rmb = KeyLogger.isUsePressed;
        if (lmb && !lastLmb) lmbClicks.addLast(now);
        if (rmb && !lastRmb) rmbClicks.addLast(now);
        lastLmb = lmb;
        lastRmb = rmb;
        lmbClicks.removeIf(t -> now - t > 1000);
        rmbClicks.removeIf(t -> now - t > 1000);

        int lmbCps = lmbClicks.size();
        int rmbCps = rmbClicks.size();

        boolean[] pressed = {
                KeyLogger.isForwardPressed, KeyLogger.isBackPressed,
                KeyLogger.isLeftPressed,   KeyLogger.isRightPressed,
                KeyLogger.isJumpPressed,   KeyLogger.isAttackPressed,
                KeyLogger.isUsePressed
        };

        float deltaTimeCapped = tick.getGameTimeDeltaTicks();
        if (deltaTimeCapped > 1) deltaTimeCapped = 1; // cap at 1 so that lerp doesn't overshoot to yellow

        int color = WidgetGeneral.getGlobalWidgetColor();

        int[] colors = new int[7];
        int[] textColors = new int[7];
        for (int i = 0; i < 7; i++) {
            blend[i] += ((pressed[i] ? 1f : 0f) - blend[i]) * LERPSPEED * deltaTimeCapped;
            colors[i]    = lerpColor(color, Tinter.tint(color, 150, 1), blend[i]);
            textColors[i] = lerpColor(0xFFFFFFFF, 0xFF000000, blend[i]);
        }

        var keyUp    = mc.options.keyUp;
        var keyDown  = mc.options.keyDown;
        var keyLeft  = mc.options.keyLeft;
        var keyRight = mc.options.keyRight;

        float cpsH  = size * 1.25f;
        float cpsW  = size * 1.5f + pad * 0.5f;

        // y is the bottom of the widget, render upward
        float cpsY   = y - cpsH;
        float spaceY = cpsY - pad - size / 2;
        float wasdY  = spaceY - pad - size;
        float wKeyY  = wasdY - pad - size;

        float spaceMargin = size * 0.2f;
        float lineH = Math.max(1, uiScale / 2f);
        float lineY = spaceY + size / 4 - lineH / 2;
        float lineX = x + spaceMargin;
        float lineW = (x + size * 3 + pad * 2 - spaceMargin) - lineX;

        int strokeSize = (int) (uiScale * 2);
        int smallStroke = strokeSize / 2;

        // W
        RoundRect.draw(canvas, x + size + pad, wKeyY, size, size, colors[0], strokeSize, strokeSize, smallStroke, smallStroke);
        // A S D
        RoundRect.draw(canvas, x,              wasdY, size, size, colors[2], strokeSize, smallStroke, smallStroke, smallStroke);
        RoundRect.draw(canvas, x + size + pad, wasdY, size, size, colors[1], smallStroke, smallStroke, smallStroke, smallStroke);
        RoundRect.draw(canvas, x + size*2+pad*2, wasdY, size, size, colors[3], smallStroke, strokeSize, smallStroke, smallStroke);
        // Space
        RoundRect.draw(canvas, x, spaceY, size * 3 + pad * 2, size / 2, colors[4], smallStroke);
        RoundRect.draw(canvas, lineX, lineY, lineW, lineH, textColors[4], 100);
        // CPS
        RoundRect.draw(canvas, x,              cpsY, cpsW, cpsH, colors[5], smallStroke, smallStroke, (int)(strokeSize / 1.2), smallStroke);
        RoundRect.draw(canvas, x + cpsW + pad, cpsY, cpsW, cpsH, colors[6], smallStroke, smallStroke, smallStroke, (int)(strokeSize / 1.2));

        //labels
        String wLabel = sanitize(keyUp.getTranslatedKeyMessage().getString());
        String aLabel = sanitize(keyLeft.getTranslatedKeyMessage().getString());
        String sLabel = sanitize(keyDown.getTranslatedKeyMessage().getString());
        String dLabel = sanitize(keyRight.getTranslatedKeyMessage().getString());

        RoundRect.drawText(canvas, wLabel, x + size + pad,   wKeyY, size, size, textColors[0], "gsans", "left", 11f);
        RoundRect.drawText(canvas, aLabel, x,                wasdY, size, size, textColors[2], "gsans", "left", 11f);
        RoundRect.drawText(canvas, sLabel, x + size + pad,   wasdY, size, size, textColors[1], "gsans", "left", 11f);
        RoundRect.drawText(canvas, dLabel, x + size*2+pad*2, wasdY, size, size, textColors[3], "gsans", "left", 11f);

        RoundRect.drawText(canvas, "LMB",            x,              cpsY,              cpsW, cpsH * 0.5f, textColors[5], "gsans", "left", 11f);
        RoundRect.drawText(canvas, sanitize(String.valueOf(lmbCps)) + " cps",  x,              cpsY + cpsH * 0.5f, cpsW, cpsH * 0.5f, textColors[5], "gsans", "left", 11f);
        RoundRect.drawText(canvas, "RMB",            x + cpsW + pad, cpsY,              cpsW, cpsH * 0.5f, textColors[6], "gsans", "left", 11f);
        RoundRect.drawText(canvas, sanitize(String.valueOf(rmbCps)) + " cps",  x + cpsW + pad, cpsY + cpsH * 0.5f, cpsW, cpsH * 0.5f, textColors[6], "gsans", "left", 11f);
    }

    @Override
    public float getWidth(int uiScale) {
        if (!isEnabled) return 0;
        Minecraft mc = Minecraft.getInstance();
        int sw = mc.getWindow().getGuiScaledWidth();
        float pad = uiScale;
        float size = sw / (90.0f / uiScale);
        return size * 3 + pad * 2;
    }

    @Override
    public float getHeight(int uiScale) {
        if (!isEnabled) return 0;
        Minecraft mc = Minecraft.getInstance();
        int sw = mc.getWindow().getGuiScaledWidth();
        float pad = uiScale;
        float size = sw / (90.0f / uiScale);
        float cpsH = size * 0.75f;
        return size * 3 + size / 2 + cpsH + pad * 4; // W + ASD + space + CPS + gaps
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
        return "Keystrokes";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }

    public static int invertColor(int color) {
        int a = (color >> 24) & 0xFF;
        int rgb = color & 0x00FFFFFF;
        return (a << 24) | (rgb ^ 0xFFFFFF);
    }

    private String sanitize(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (i + 1 < input.length() && Character.isLowSurrogate(input.charAt(i + 1))) {
                    sb.append(c);
                    sb.append(input.charAt(++i));
                } else {
                    sb.append('\uFFFD');
                }
            } else if (Character.isLowSurrogate(c)) {
                sb.append('\uFFFD');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}