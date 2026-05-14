package dev.bsprout.btweaks.client;

import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.bsprout.btweaks.client.config.ConfigManager;
import dev.bsprout.btweaks.client.config.ConfigWindow;
import dev.bsprout.btweaks.client.qol.Calculator;
import dev.bsprout.btweaks.client.widgets.*;
import io.github.humbleui.skija.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static jdk.jpackage.internal.Log.error;
import static org.spongepowered.asm.mixin.injection.selectors.ElementNode.listOf;

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
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

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

            dispatcher.register(ClientCommandManager.literal("dump_skija")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(Component.literal("§eDumping in 5 seconds... Switch to your menu now!"));

                        // Schedule the task
                        SCHEDULER.schedule(() -> {
                            // We MUST execute the dump on the main render thread
                            Minecraft.getInstance().execute(() -> {
                                if (BtweaksClient.surface != null) {
                                    debugDump.dump(BtweaksClient.surface, "delayed_" + System.currentTimeMillis());
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.literal("§aDump complete!"));
                                }
                            });
                        }, 5, TimeUnit.SECONDS);

                        return 1;
                    }));
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
            GlTexture mainTarget = (GlTexture) Minecraft.getInstance().getMainRenderTarget().getColorTexture();

            if (BtweaksClient.context == null) {
                BtweaksClient.context = DirectContext.makeGL();
            }

            if (BtweaksClient.context == null) return;

            if (surface != null) surface.close();
            if (renderTarget != null) renderTarget.close();

            int fbId = mainTarget.glId();

            renderTarget = BackendRenderTarget.makeGL(
                    width, height, 0, 8, fbId,
                    FramebufferFormat.GR_GL_RGBA8
            );

            surface = Surface.makeFromBackendRenderTarget(
                    BtweaksClient.context, renderTarget,
                    SurfaceOrigin.BOTTOM_LEFT,
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
    public static int getFboId(RenderTarget frameBuffer) {
        GpuDeviceAccessor device = RenderSystem.getDevice();
        GlDevice backend = device.backend;
        var glCommandEncoder = backend.createCommandEncoder();

        val colorAttachments = listOf(
                Attachment(
                        frameBuffer.colorTextureView ?: error("attempted to use a framebuffer without a color texture for presentation"),
                Optional.empty<Vector4fc>()
            )
        )
        val renderPassColorTextures = Reflection.field<MutableList<FrameBufferAttachment?>>(glCommandEncoder, "renderPassColorTextures")

        renderPassColorTextures.clear()

        for (colorAttachment in colorAttachments) {
            renderPassColorTextures.add(colorAttachment.textureView() as GlTextureView)
        }

        checkNotNull(frameBuffer.depthTexture)

        val depthAttachment: Attachment<OptionalDouble> = Attachment(frameBuffer.depthTextureView ?: error("no depth?"), OptionalDouble.empty())

        return backend.frameBufferCache().getFbo(
                backend.directStateAccess(),
                renderPassColorTextures,
                depthAttachment.textureView as GlTextureView
        )
    }
}