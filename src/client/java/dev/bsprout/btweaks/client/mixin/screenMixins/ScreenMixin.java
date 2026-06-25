package dev.bsprout.btweaks.client.mixin.screenMixins;

import dev.bsprout.btweaks.client.helpers.ClipboardHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "defaultHandleClickEvent", at = @At("HEAD"), cancellable = true)
    private static void btweaks$interceptImageCopy(ClickEvent clickEvent, Minecraft minecraft, Screen screen, CallbackInfo ci) {
        if (clickEvent instanceof ClickEvent.CopyToClipboard copyEvent) {
            String value = copyEvent.value();

            if (value.endsWith("#is_btweaks_copy")) {
                File imageFile = new File(value.replace("#is_btweaks_copy", ""));

                ClipboardHelper.copyImageToClipboard(imageFile);

                ci.cancel();
            }
        }
    }
}