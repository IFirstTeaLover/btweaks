package dev.bsprout.btweaks.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

public interface Widget {
    void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick);
    float getWidth(int uiScale);
    float getHeight(int uiScale);

    boolean isEnabled();
    void setEnabled(boolean state);
    String getName();

    void setX(int x);
    void setY(int y);
}