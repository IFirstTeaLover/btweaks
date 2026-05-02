package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.KeyLogger;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import java.util.ArrayDeque;
import java.util.Deque;

public class Keystroke implements Widget {

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

        int[] colors = new int[7];
        for (int i = 0; i < 7; i++) {
            blend[i] += ((pressed[i] ? 1f : 0f) - blend[i]) * LERPSPEED * tick.getGameTimeDeltaTicks() / 16;
            colors[i] = lerpColor(0x80262626, 0x80D9D9D9, blend[i]);
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

        // W
        RoundRect.draw(ctx, x + size + pad, wKeyY, size, size, colors[0]);
        // A S D
        RoundRect.draw(ctx, x,              wasdY, size, size, colors[2]);
        RoundRect.draw(ctx, x + size + pad, wasdY, size, size, colors[1]);
        RoundRect.draw(ctx, x + size*2+pad*2, wasdY, size, size, colors[3]);
        // Space
        RoundRect.draw(ctx, x, spaceY, size * 3 + pad * 2, size / 2, colors[4]);
        RoundRect.draw(ctx, lineX, lineY, lineW, lineH, 0xFFFFFFFF);
        // CPS
        RoundRect.draw(ctx, x,              cpsY, cpsW, cpsH, colors[5]);
        RoundRect.draw(ctx, x + cpsW + pad, cpsY, cpsW, cpsH, colors[6]);

        // Labels
        RoundRect.drawText(ctx, keyUp.getTranslatedKeyMessage().getString(),    x + size + pad,   wKeyY, size, size, 0xFFFFFFFF);
        RoundRect.drawText(ctx, keyLeft.getTranslatedKeyMessage().getString(),  x,                wasdY, size, size, 0xFFFFFFFF);
        RoundRect.drawText(ctx, keyDown.getTranslatedKeyMessage().getString(),  x + size + pad,   wasdY, size, size, 0xFFFFFFFF);
        RoundRect.drawText(ctx, keyRight.getTranslatedKeyMessage().getString(), x + size*2+pad*2, wasdY, size, size, 0xFFFFFFFF);

        RoundRect.drawText(ctx, "LMB",            x,              cpsY,              cpsW, cpsH * 0.5f, 0xFFFFFFFF);
        RoundRect.drawText(ctx, lmbCps + " cps",  x,              cpsY + cpsH * 0.5f, cpsW, cpsH * 0.5f, 0xFFFFFFFF);
        RoundRect.drawText(ctx, "RMB",            x + cpsW + pad, cpsY,              cpsW, cpsH * 0.5f, 0xFFFFFFFF);
        RoundRect.drawText(ctx, rmbCps + " cps",  x + cpsW + pad, cpsY + cpsH * 0.5f, cpsW, cpsH * 0.5f, 0xFFFFFFFF);
    }

    @Override
    public float getWidth(int uiScale) {
        Minecraft mc = Minecraft.getInstance();
        int sw = mc.getWindow().getGuiScaledWidth();
        float pad = uiScale;
        float size = sw / (90.0f / uiScale);
        return size * 3 + pad * 2;
    }

    @Override
    public float getHeight(int uiScale) {
        Minecraft mc = Minecraft.getInstance();
        int sw = mc.getWindow().getGuiScaledWidth();
        float pad = uiScale;
        float size = sw / (90.0f / uiScale);
        float cpsH = size * 0.75f;
        return size * 3 + size / 2 + cpsH + pad * 4; // W + ASD + space + CPS + gaps
    }
}