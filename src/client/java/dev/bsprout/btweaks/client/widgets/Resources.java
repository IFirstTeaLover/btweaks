package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Items;

import static dev.bsprout.btweaks.client.AssetManager.getJersey;
import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import static dev.bsprout.btweaks.client.widgets.FPS.Jersey;

import dev.bsprout.btweaks.client.extras.BedwarsDetector;

public class Resources implements Widget {
    private boolean isEnabled;
    private final boolean isDev = true;

    private boolean shouldRender() {
        if (!isEnabled || mc.player == null) return false;
        return isDev || BedwarsDetector.isInBedwars();
    }

    private int[] getResourceAmounts() {
        if (mc.player == null) return new int[]{0, 0, 0, 0};
        return new int[]{
                mc.player.getInventory().countItem(Items.IRON_INGOT),
                mc.player.getInventory().countItem(Items.GOLD_INGOT),
                mc.player.getInventory().countItem(Items.DIAMOND),
                mc.player.getInventory().countItem(Items.EMERALD)
        };
    }

    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick, BRender bRender) {
        if (mc.getDebugOverlay().showDebugScreen()) return;
        if (!shouldRender()) return;

        int[] amounts = getResourceAmounts();
        float currentY = y;
        int fontSize = 10;
        int padding = 6;
        int color = WidgetGeneral.getGlobalWidgetColor();

        for (int amount : amounts) {
            if (amount > 0) {
                String text = String.valueOf(amount);
                float rectWidth = Jersey.textSize(text, fontSize) + (padding * 2);

                bRender.roundRect((int) x, (int) currentY, (int) rectWidth, 15, color, uiScale, 1);
                bRender.drawText(getJersey(), text, x + padding, currentY + fontSize, fontSize, 0xFFFFFFFF, 4);

                currentY += 18; // 15 height + 3 gap
            }
        }
    }

    @Override
    public float getWidth(int uiScale) {
        if (!shouldRender()) return 0;

        int[] amounts = getResourceAmounts();
        float maxWidth = 0;
        int padding = 6;

        for (int amount : amounts) {
            if (amount > 0) {
                float rectWidth = Jersey.textSize(String.valueOf(amount), 10) + (padding * 2);
                if (rectWidth > maxWidth) maxWidth = rectWidth;
            }
        }
        return maxWidth;
    }

    @Override
    public float getHeight(int uiScale) {
        if (!shouldRender()) return 0;

        int[] amounts = getResourceAmounts();
        int activeRows = 0;

        for (int amount : amounts) {
            if (amount > 0) activeRows++;
        }

        if (activeRows == 0) return 0;
        // (activeRows * 15px height) + ((activeRows - 1) * 3px gaps)
        return (activeRows * 15) + ((activeRows - 1) * 3);
    }

    @Override
    public boolean isEnabled() {
        isEnabled = ConfigManager.getBoolean(this.getName(), true);
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        ConfigManager.set(this.getName(), enabled);
        this.isEnabled = enabled;
    }

    @Override
    public String getName() {
        return "Resources";
    }

    @Override
    public void setX(int x) {}

    @Override
    public void setY(int y) {}
}