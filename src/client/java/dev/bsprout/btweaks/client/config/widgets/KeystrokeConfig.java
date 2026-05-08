package dev.bsprout.btweaks.client.config.widgets;

import dev.bsprout.btweaks.client.WidgetInstance;
import dev.bsprout.btweaks.client.config.ConfigWindow;

public class KeystrokeConfig {
    public static void showKeystrokeConfig(ConfigWindow screen, WidgetInstance inst){
        screen.newCheckmark(inst, "Enable keystrokes", "Show keystrokes.");
    }
}
