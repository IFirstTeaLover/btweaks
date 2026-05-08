package dev.bsprout.btweaks.client.config.widgets;

import dev.bsprout.btweaks.client.WidgetInstance;
import dev.bsprout.btweaks.client.config.ConfigWindow;

public class FPSConfig {
    public static void showFpsDisplayConfig(ConfigWindow screen, WidgetInstance inst){
        screen.newCheckmark(inst, "Enable FPS Display", "Show your current FPS.");
    }
}
