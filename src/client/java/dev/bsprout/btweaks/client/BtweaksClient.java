package dev.bsprout.btweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.config.ConfigWindow;
import dev.bsprout.btweaks.client.qol.Calculator;
import dev.bsprout.btweaks.client.widgets.*;
import io.github.humbleui.skija.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.resources.Identifier;
import org.lwjgl.opengl.GL11;
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

    public static DirectContext context;
    public static BackendRenderTarget renderTarget;
    public static Surface surface;
    public static Canvas canvas;
    public static int lastWidth = -1;
    public static int lastHeight = -1;
    private static int skijaAttempts = 0;

    @Override
    public void onInitializeClient() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("[btweaks] Initializing KeyLogger, WorldCallback, Config, Calculator!");

        KeyLogger.register();
        WorldCallback.register();
        ConfigManager.load();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.execute(ConfigWindow::initializeWidgetsFromConfig);
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            Calculator.register(dispatcher);
        });

        long duration = System.currentTimeMillis() - startTime;

        LOGGER.info("[btweaks] Initialized stage 1 successfully in {}ms!", duration);


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

        duration = System.currentTimeMillis() - startTime;

        LOGGER.info("[btweaks] Initialized successfully in {}ms!", duration);
    }

    public static void setupSkia(int width, int height) {
        try {
            if (BtweaksClient.context == null) {
                BtweaksClient.context = DirectContext.makeGL();
            }

            if (BtweaksClient.context == null) return;

            if (surface != null) surface.close();
            if (renderTarget != null) renderTarget.close();

            int fbId = GL11.glGetInteger(0x8CA6);

            renderTarget = BackendRenderTarget.makeGL(
                    width, height, 0, 8, fbId,
                    FramebufferFormat.GR_GL_RGBA8
            );

            surface = Surface.makeFromBackendRenderTarget(
                    BtweaksClient.context, renderTarget,
                    SurfaceOrigin.TOP_LEFT,
                    ColorType.RGBA_8888,
                    ColorSpace.getSRGB()
            );

            canvas = surface.getCanvas();


            lastWidth = width;
            lastHeight = height;

            LOGGER.info("[btweaks] Skia context bound successfully at {}x{}!", width, height);
        } catch (Exception e) {
            skijaAttempts++;
        }
    }
}