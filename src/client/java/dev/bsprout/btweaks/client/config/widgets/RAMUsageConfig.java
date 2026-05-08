package dev.bsprout.btweaks.client.config.widgets;

import dev.bsprout.btweaks.client.WidgetInstance;
import dev.bsprout.btweaks.client.config.ConfigWindow;

public class RAMUsageConfig {
    public static void showRAMUsageConfig(ConfigWindow screen, WidgetInstance inst){
        screen.newCheckmark(inst, "Enable RAM Usage Display", "Show your RAM usage.");
    }
}
