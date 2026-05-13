package dev.bsprout.btweaks.client.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.bsprout.btweaks.client.BtweaksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "updateDisplay", at = @At("HEAD"))
    private void onUpdateDisplay(CallbackInfo ci) {
        int pixelWidth = ((Window)(Object)this).getWidth();
        int pixelHeight = ((Window)(Object)this).getHeight();

        if (BtweaksClient.canvas == null ||
                BtweaksClient.lastWidth != pixelWidth ||
                BtweaksClient.lastHeight != pixelHeight) {

            BtweaksClient.setupSkia(pixelWidth, pixelHeight);
        }
    }
}