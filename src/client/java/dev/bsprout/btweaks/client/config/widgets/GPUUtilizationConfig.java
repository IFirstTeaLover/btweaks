package dev.bsprout.btweaks.client.config.widgets;

import dev.bsprout.btweaks.client.WidgetInstance;
import dev.bsprout.btweaks.client.config.ConfigWindow;

public class GPUUtilizationConfig {
    public static void showGPUUtilizationConfig(ConfigWindow screen, WidgetInstance inst){
        screen.newCheckmark(inst, "Enable GPU Utilization Display", "Show your GPU Utilization usage.");
    }
}
