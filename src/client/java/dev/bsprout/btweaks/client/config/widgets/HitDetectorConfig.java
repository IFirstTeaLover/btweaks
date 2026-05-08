package dev.bsprout.btweaks.client.config.widgets;

import dev.bsprout.btweaks.client.WidgetInstance;
import dev.bsprout.btweaks.client.config.ConfigWindow;

public class HitDetectorConfig {
    public static void showHitDetectorConfig(ConfigWindow screen, WidgetInstance inst){
        screen.newCheckmark(inst, "Enable Hit Detector", "Show can/can't hit text while looking at something.");
    }
}
