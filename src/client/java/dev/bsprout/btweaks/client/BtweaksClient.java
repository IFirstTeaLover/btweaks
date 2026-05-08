package dev.bsprout.btweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.config.ConfigWindow;
import dev.bsprout.btweaks.client.widgets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BtweaksClient implements ClientModInitializer {
    public static final Minecraft mc = Minecraft.getInstance();
    public static WidgetRenderer renderer;
    public static final Logger LOGGER = LoggerFactory.getLogger("btweaks");
    public static final KeyMapping openConfigKey = new KeyMapping(
            "key.btweaks.open_config",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_RSHIFT,
            new KeyMapping.Category(Identifier.fromNamespaceAndPath("btweaks", "main"))
    );

    @Override
    public void onInitializeClient() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("[btweaks] Initializing!");
        KeyLogger.register();
        WorldCallback.register();
        ConfigManager.load();


        LOGGER.info("[btweaks] Initializing widget renderer!");
        renderer = new WidgetRenderer();

        LOGGER.info("[btweaks] Initializing widgets!");
        renderer.add(new Keystroke(), 4, 4, WidgetInstance.Anchor.BOTTOM_RIGHT);
        renderer.add(new FPS(), -1, -1, WidgetInstance.Anchor.TOP_LEFT);
        renderer.add(new Ping(), -1, -1, WidgetInstance.Anchor.TOP_LEFT);
        renderer.add(new GPUUtilization(), -1, -1, WidgetInstance.Anchor.TOP_LEFT);
        renderer.add(new Coordinates(), -1, -1, WidgetInstance.Anchor.TOP_LEFT);
        renderer.add(new RAMUsage(), -1, -1, WidgetInstance.Anchor.TOP_RIGHT);
        renderer.add(new HitDetector(), 0, 30, WidgetInstance.Anchor.CENTER);

        HudRenderCallback.EVENT.register(renderer);

        long duration = System.currentTimeMillis() - startTime;

        LOGGER.info("[btweaks] Initialized successfully in {}ms!", duration);
    }
}