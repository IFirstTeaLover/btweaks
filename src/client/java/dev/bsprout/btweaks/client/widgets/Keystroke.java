package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.KeyLogger;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.Tinter;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import java.util.ArrayDeque;
import java.util.Deque;

import static dev.bsprout.btweaks.client.widgets.FPS.Jersey;

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
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick, BRender bRender) {
        if (!isEnabled) return;
        Minecraft mc = Minecraft.getInstance();
        long now = System.currentTimeMillis();
        int sw = mc.getWindow().getGuiScaledWidth();
        int color = WidgetGeneral.getGlobalWidgetColor();
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
        bRender.roundRect((int) (x + size + pad), (int) wKeyY, (int) size, (int) size, colors[0], strokeSize, strokeSize, smallStroke, smallStroke, 1);
        // A S D
        bRender.roundRect((int) x, (int) wasdY, (int) size, (int) size, colors[2], strokeSize, smallStroke, smallStroke, smallStroke, 1);
        bRender.roundRect((int) (x + size + pad), (int) wasdY, (int) size, (int) size, colors[1], smallStroke, smallStroke, smallStroke, smallStroke, 1);
        bRender.roundRect((int) (x + size*2+pad*2), (int) wasdY, (int) size, (int) size, colors[3], smallStroke,strokeSize, smallStroke, smallStroke, 1);
        // Space
        bRender.roundRect((int) x, (int) spaceY, (int)(size * 3 + pad * 2), (int)(size / 2), colors[4], smallStroke, 1);
        bRender.roundRect((int)lineX, (int) lineY, (int) lineW, (int) lineH, textColors[4], (int) lineH/2, 1);
        // CPS
        bRender.roundRect((int) x,                (int) cpsY, (int) cpsW, (int) cpsH, colors[5], smallStroke, smallStroke, smallStroke, strokeSize, 1);
        bRender.roundRect((int) (x + cpsW + pad), (int) cpsY, (int) cpsW, (int) cpsH, colors[6], smallStroke, smallStroke, strokeSize, smallStroke, 1);

        // Labels
        float fontSize = (float) (size / 1.75);

        // W Text
        String wText = keyUp.getTranslatedKeyMessage().getString();
        bRender.drawText(Jersey, wText, x + size + pad + (size - Jersey.textSize(wText, fontSize)) / 2 - 1, wKeyY + fontSize, fontSize, textColors[0], 4);

        // A Text
        String aText = keyLeft.getTranslatedKeyMessage().getString();
        bRender.drawText(Jersey, aText, x + (size - Jersey.textSize(aText, fontSize)) / 2 - 1, wasdY + fontSize, fontSize, textColors[2], 4);

        // S Text
        String sText = keyDown.getTranslatedKeyMessage().getString();
        bRender.drawText(Jersey, sText, x + size + pad + (size - Jersey.textSize(sText, fontSize)) / 2 - 1, wasdY + fontSize, fontSize, textColors[1], 4);

        // D Text
        String dText = keyRight.getTranslatedKeyMessage().getString();
        bRender.drawText(Jersey, dText, x + size * 2 + pad * 2 + (size - Jersey.textSize(dText, fontSize)) / 2 - 1, wasdY + fontSize, fontSize, textColors[3], 4);

        // Mouse buttons & CPS labels
        float mouseFontSize = fontSize * 0.9f;
        String lmbText = "LMB";
        String lmbCpsText = lmbCps + " CPS";
        bRender.drawText(Jersey, lmbText, x + (cpsW - Jersey.textSize(lmbText, mouseFontSize)) / 2 - 1, cpsY + mouseFontSize, mouseFontSize, textColors[5], 4);
        bRender.drawText(Jersey, lmbCpsText, x + (cpsW - Jersey.textSize(lmbCpsText, mouseFontSize * 0.7f)) / 2 - 1, cpsY + cpsH * 0.5f + mouseFontSize * 0.7f - 2, mouseFontSize * 0.8f, textColors[5], 4);

        String rmbText = "RMB";
        String rmbCpsText = rmbCps + " CPS";
        bRender.drawText(Jersey, rmbText, x + cpsW + pad + (cpsW - Jersey.textSize(rmbText, mouseFontSize)) / 2 - 1, cpsY + mouseFontSize, mouseFontSize, textColors[6], 4);
        bRender.drawText(Jersey, rmbCpsText, x + cpsW + pad + (cpsW - Jersey.textSize(rmbCpsText, mouseFontSize * 0.7f)) / 2 - 1, cpsY + cpsH * 0.5f + mouseFontSize * 0.7f - 2, mouseFontSize * 0.8f, textColors[6], 4);}

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
}