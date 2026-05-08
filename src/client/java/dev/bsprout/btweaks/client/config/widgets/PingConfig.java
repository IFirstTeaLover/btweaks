package dev.bsprout.btweaks.client.config.widgets;

import dev.bsprout.btweaks.client.WidgetInstance;
import dev.bsprout.btweaks.client.config.ConfigWindow;

public class PingConfig {
    public static void showPingConfig(ConfigWindow screen, WidgetInstance inst){
        screen.newCheckmark(inst, "Enable ping Display", "Show your ping.");
    }
}
