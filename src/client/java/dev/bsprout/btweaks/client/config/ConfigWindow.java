package dev.bsprout.btweaks.client.config;

import dev.bsprout.btweaks.client.RoundRect;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import static dev.bsprout.btweaks.client.KeyLogger.configOpened;

public class ConfigWindow implements HudRenderCallback {
    public static float targetScale = 0.0f;
    private float animationProgress = 0f; // 0.0 to 1.0
    private final float animationSpeed = 4.0f;

    @Override
    public void onHudRender(GuiGraphics ctx, DeltaTracker tickCounter) {
        float delta = tickCounter.getGameTimeDeltaTicks() / 20f;

        if (configOpened) {
            animationProgress = Math.min(1.0f, animationProgress + (delta * animationSpeed));
        } else {
            animationProgress = Math.max(0.0f, animationProgress - (delta * animationSpeed));
        }

        float configScale = (float) Math.sin((animationProgress * Math.PI) / 2);

        if (configScale <= 0) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        float fullW = sw * 0.75f;
        float fullH = sh * 0.75f;

        float currentW = fullW * configScale;
        float currentH = fullH * configScale;

        float x = (sw - currentW) / 2f;
        float y = (sh - currentH) / 2f;

        // ———————— Render ————————
        RoundRect.draw(ctx, x, y, currentW, currentH, 0xe66a84e6);
    }
}