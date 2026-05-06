package dev.bsprout.btweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import dev.bsprout.btweaks.client.config.ConfigWindow;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

import static dev.bsprout.btweaks.client.BtweaksClient.openConfigKey;
import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class KeyLogger {
    public static boolean isForwardPressed = false;
    public static boolean isBackPressed    = false;
    public static boolean isLeftPressed    = false;
    public static boolean isRightPressed   = false;
    public static boolean isJumpPressed    = false;
    public static boolean isSneakPressed   = false;
    public static boolean isSprintPressed  = false;

    public static boolean isAttackPressed = false;
    public static boolean isUsePressed    = false;
    public static boolean configOpened    = false;
    private static boolean keyWasDown = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getWindow() == null) return;

            Window window = client.getWindow();

            isForwardPressed  = InputConstants.isKeyDown(window, InputConstants.getKey(client.options.keyUp.saveString()).getValue());
            isBackPressed     = InputConstants.isKeyDown(window, InputConstants.getKey(client.options.keyDown.saveString()).getValue());
            isLeftPressed     = InputConstants.isKeyDown(window, InputConstants.getKey(client.options.keyLeft.saveString()).getValue());
            isRightPressed    = InputConstants.isKeyDown(window, InputConstants.getKey(client.options.keyRight.saveString()).getValue());
            isJumpPressed     = InputConstants.isKeyDown(window, InputConstants.getKey(client.options.keyJump.saveString()).getValue());
            isSneakPressed    = InputConstants.isKeyDown(window, InputConstants.getKey(client.options.keyShift.saveString()).getValue());
            isSprintPressed   = InputConstants.isKeyDown(window, InputConstants.getKey(client.options.keySprint.saveString()).getValue());

            isAttackPressed   = GLFW.glfwGetMouseButton(window.handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            isUsePressed      = GLFW.glfwGetMouseButton(window.handle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

            while (openConfigKey.consumeClick()) {
                if (mc.screen instanceof ConfigWindow) {
                    mc.screen.onClose();
                } else {
                    mc.setScreen(new ConfigWindow());
                }
            }
        });
    }

}