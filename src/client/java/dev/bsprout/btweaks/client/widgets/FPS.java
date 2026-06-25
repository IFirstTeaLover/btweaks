package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.brapi.client.BFont;
import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import static dev.bsprout.btweaks.client.AssetManager.getJersey;
import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class FPS implements Widget {
    private boolean isEnabled;
    public static BFont Jersey;

    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick, BRender bRender) {
        Jersey = getJersey();

        if (mc.getDebugOverlay().showDebugScreen()) return;
        if (!isEnabled) return;

        String text = "FPS: " + mc.getFps();

        int color = WidgetGeneral.getGlobalWidgetColor();

        int height = 15;
        int padding = 6;
        float fontSize = 10;
        float textWidth = Jersey.textSize(text, fontSize);
        float rectWidth = textWidth + padding * 2;
        bRender.roundRect((int) x, (int) y, (int) rectWidth, height, color, uiScale, 1);
        bRender.drawText(Jersey, text, x + padding, y + fontSize,  fontSize, 0xFFFFFFFF, 4);
    }

    @Override
    public float getWidth(int uiScale) {
        if (Jersey == null){
            Jersey = new BFont(Identifier.fromNamespaceAndPath("btweaks", "font/jersey20.ttf"));
        }

        if (!isEnabled) return 0;
        return Jersey.textSize("FPS " + mc.getFps(), 10) + uiScale * 4;
    }

    @Override
    public float getHeight(int uiScale) {
        if (!isEnabled) return 0;
        return 15;
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
        return "Framerate display";
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}
