package dev.bsprout.btweaks.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

public interface Widget {
    void render(GuiGraphics ctx, int sw, int sh, int uiScale, float x, float y, DeltaTracker tick);
}