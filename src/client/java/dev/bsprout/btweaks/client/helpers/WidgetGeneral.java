package dev.bsprout.btweaks.client.helpers;

import dev.bsprout.btweaks.client.config.ConfigManager;

public class WidgetGeneral {
    public static int getGlobalWidgetColor(){
        return ConfigManager.getInt("globalWidgetColor", 0x80262626);
    }

    public static void setGlobalWidgetColor(int color){
        ConfigManager.set("globalWidgetColor", color);
    }
}