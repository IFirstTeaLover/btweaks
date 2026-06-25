package dev.bsprout.btweaks.client.mixin.screenMixins;

import dev.bsprout.btweaks.client.LoadingWindow;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftEarlyMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInitFinished(CallbackInfo ci) {
        LoadingWindow.close();
    }
}