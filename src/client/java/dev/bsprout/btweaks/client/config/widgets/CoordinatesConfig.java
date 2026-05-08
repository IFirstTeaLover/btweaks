package dev.bsprout.btweaks.client.config.widgets;

import dev.bsprout.btweaks.client.WidgetInstance;
import dev.bsprout.btweaks.client.config.ConfigWindow;

public class CoordinatesConfig {
    public static void showCoordinatesConfig(ConfigWindow screen, WidgetInstance inst) {
        screen.newCheckmark(inst, "Enable Coordinates", "Show your current X, Y, Z position on screen.");
    }
}
