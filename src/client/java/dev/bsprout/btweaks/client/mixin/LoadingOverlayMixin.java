package dev.bsprout.btweaks.client.mixin;

import dev.bsprout.btweaks.client.RoundRect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.function.IntSupplier;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Shadow
    private float currentProgress;

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Ljava/util/function/IntSupplier;getAsInt()I")
    )
    private int btweaks$forceBlack(IntSupplier instance) {
        return 0xFF0A0A0A;
    }

    /**
     * @author bsprout
     * @reason making progress bar round
     */
    @Overwrite
    private void drawProgressBar(GuiGraphics guiGraphics, int i, int j, int k, int l, float f) {
        int alpha = Math.round(f * 255.0F);
        int color = ARGB.color(alpha, 255, 255, 255);

        RoundRect.draw(guiGraphics, i, j, k - i, l - j, color, 4);

        float maxInnerWidth = (k - i) - 4;
        float progressWidth = maxInnerWidth * this.currentProgress;

        int trackColor = 0xFF0A0A0A;
        RoundRect.draw(guiGraphics, i + 1, j + 1, (k - i) - 2, (l - j) - 2, trackColor, 3);

        if (progressWidth > 0) {
            if (isJune()) {
                // gay bar
                float timeOffset = (System.currentTimeMillis() % 2000) / 2000f;

                for (int x = 0; x < progressWidth; x++) {
                    float hue = ((float) x / (float) maxInnerWidth) + timeOffset;
                    int rgb = java.awt.Color.HSBtoRGB(hue % 1.0f, 0.7f, 1.0f);
                    color = (alpha << 24) | (rgb & 0xFFFFFF);

                    RoundRect.draw(guiGraphics, i + 2 + x, j + 2, 5, l - j - 4, color, 2);
                }
            } else {
                // non-gay bar
                RoundRect.draw(guiGraphics, i + 2, j + 2, progressWidth, (l - j) - 4, color, 2);
            }
        }
    }

    private boolean isJune() {
        return LocalDate.now().getMonth() == Month.JUNE;
    }

//    @Redirect(
//            method = "tick",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ReloadInstance;isDone()Z")
//    )
//    private boolean btweaks$neverDone(ReloadInstance instance) {
//        return false;
//    } test purposes!
}