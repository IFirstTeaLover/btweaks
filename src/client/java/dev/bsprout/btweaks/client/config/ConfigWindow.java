package dev.bsprout.btweaks.client.config;

import dev.bsprout.btweaks.client.RoundRect;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import static dev.bsprout.btweaks.client.KeyLogger.configOpened;

public class ConfigWindow implements HudRenderCallback {
    private float animationProgress = 0f;
    private final float animationSpeed = 4.0f;

    // sidebar width as fraction of window
    private static final float SIDEBAR_FRAC = 0.22f;

    @Override
    public void onHudRender(GuiGraphics ctx, DeltaTracker tickCounter) {
        float delta = tickCounter.getGameTimeDeltaTicks() / 20f;

        if (configOpened) {
            animationProgress = Math.min(1.0f, animationProgress + delta * animationSpeed);
        } else {
            animationProgress = Math.max(0.0f, animationProgress - delta * animationSpeed);
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

        float sideW = currentW * SIDEBAR_FRAC;
        float contentX = x + sideW;
        float contentW = currentW - sideW;
        float pad = 8 * configScale;

        // ——— Main background ———
        RoundRect.draw(ctx, x, y, currentW, currentH, 0xCC1A1A1A); // radius: 10

        // ——— Sidebar background ———
        RoundRect.draw(ctx, x, y, sideW, currentH, 0xCC141414); // radius: 10

        // ——— Sidebar: mod name/logo area ———
        float logoH = currentH * 0.1f;
        RoundRect.drawText(ctx, "BTWEAKS", x, y, sideW, logoH, 0xFF4A9EFF);

        // ——— Sidebar sections ———
        float sideY = y + logoH + pad;
        float sectionH = currentH * 0.06f;

        // section label
        RoundRect.drawText(ctx, "CONFIGURATION", x + pad, sideY, sideW - pad * 2, sectionH * 0.5f, 0xFF666666);
        sideY += sectionH * 0.5f;

        // sidebar items
        String[] sideItems = {"HUD", "Keybinds", "Combat", "Visual"};
        for (int i = 0; i < sideItems.length; i++) {
            boolean selected = i == 0; // TODO: track selection
            int itemColor = selected ? 0xFF4A9EFF : 0xFFAAAAAA;
            int bgColor   = selected ? 0x334A9EFF : 0x00000000;
            if (selected) RoundRect.draw(ctx, x + pad, sideY, sideW - pad * 2, sectionH, bgColor); // radius: 6
            RoundRect.drawText(ctx, sideItems[i], x + pad * 2, sideY, sideW - pad * 4, sectionH, itemColor);
            sideY += sectionH + pad * 0.5f;
        }

        // ——— Separator line ———
        ctx.fill((int)(contentX), (int)(y + pad), (int)(contentX + 1), (int)(y + currentH - pad), 0xFF2A2A2A);

        // ——— Content area: title ———
        float titleH = currentH * 0.1f;
        RoundRect.drawText(ctx, "HUD", contentX + pad, y, contentW - pad * 2, titleH, 0xFFFFFFFF);

        float rowH = currentH * 0.1f;
        float rowY = y + titleH + pad;
        float rowW = contentW - pad * 2;

        Object[][] configs = {
                {"Keystroke", "Shows WASD keys and CPS counters", true},
                {"FPS", "Displays frames per second", true},
                {"Coordinates", "Shows XYZ player position", false},
                {"RAM Usage", "Displays memory usage", false},
                {"Hit Detector", "Shows attack range indicator", true},
        };

        for (Object[] config : configs) {
            String name = (String) config[0];
            String desc = (String) config[1];
            boolean enabled = (boolean) config[2];

            // row background
            RoundRect.draw(ctx, contentX + pad, rowY, rowW, rowH, 0xFF252525);

            // name
            float nameH = rowH * 0.5f;
            RoundRect.drawText(ctx, name, contentX + pad * 2, rowY, rowW * 0.6f, nameH, 0xFFFFFFFF);

            // description
            RoundRect.drawText(ctx, desc, contentX + pad * 2, rowY + nameH, rowW * 0.6f, nameH, 0xFF888888);

            // OFF label
            float toggleW = rowW * 0.08f;
            float toggleX = contentX + pad + rowW - toggleW * 2 - pad;
            RoundRect.drawText(ctx, "OFF", toggleX, rowY, toggleW, rowH, enabled ? 0xFF555555 : 0xFFFFFFFF);

            // ON label
            float onX = toggleX + toggleW;
            RoundRect.draw(ctx, onX, rowY + rowH * 0.2f, toggleW, rowH * 0.6f, enabled ? 0xFF4A9EFF : 0xFF333333);
            RoundRect.drawText(ctx, "ON", onX, rowY + rowH * 0.2f, toggleW, rowH * 0.6f, enabled ? 0xFFFFFFFF : 0xFF555555);

            rowY += rowH + pad * 0.5f;
        }

        // ——— Bottom bar: close hint ———
        float bottomH = currentH * 0.07f;
        float bottomY = y + currentH - bottomH;
        RoundRect.draw(ctx, x, bottomY, currentW, bottomH, 0xFF111111); // radius: 10
        RoundRect.drawText(ctx, "Press [KEY] to close", x + pad, bottomY, currentW - pad * 2, bottomH, 0xFF555555);
    }
}