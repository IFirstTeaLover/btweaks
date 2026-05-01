package dev.bsprout.btweaks.client;

import dev.bsprout.btweaks.client.widgets.FPS;
import dev.bsprout.btweaks.client.widgets.Keystroke;
import dev.bsprout.btweaks.client.widgets.Coordinates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class BtweaksClient implements ClientModInitializer {
    public static final Minecraft mc = Minecraft.getInstance();

    @Override
    public void onInitializeClient() {
        KeyLogger.register();

        WidgetRenderer renderer = new WidgetRenderer();
        renderer.add(new Keystroke(), 4, 4, WidgetInstance.Anchor.BOTTOM_LEFT);
        renderer.add(new FPS(), 4, 4, WidgetInstance.Anchor.TOP_LEFT);
        renderer.add(new Coordinates(), 4, 4, WidgetInstance.Anchor.TOP_LEFT);

        HudRenderCallback.EVENT.register(renderer);
    }
}