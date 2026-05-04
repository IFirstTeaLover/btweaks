package dev.bsprout.btweaks.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.function.IntSupplier;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Ljava/util/function/IntSupplier;getAsInt()I")
    )
    private int btweaks$forceBlack(IntSupplier instance) {
        return 0xFF1C1C1C;
    }
}