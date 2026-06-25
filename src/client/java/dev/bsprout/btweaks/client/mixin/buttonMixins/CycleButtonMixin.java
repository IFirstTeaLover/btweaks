package dev.bsprout.btweaks.client.mixin.buttonMixins;

import dev.bsprout.brapi.client.BFont;
import dev.bsprout.brapi.client.BRender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.bsprout.btweaks.client.AssetManager.getJersey;

@Mixin(CycleButton.class)
@Environment(EnvType.CLIENT)
public abstract class CycleButtonMixin {
    @Unique
    private float btweaks$hoverProgress = 0f;

    @Unique
    private static final BRender r = new BRender();

    @Inject(method = "renderContents", at = @At("HEAD"), cancellable = true)
    private void btweaks$renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        AbstractWidget self = (AbstractWidget)(Object) this;

        boolean hovered = self.isHovered();
        boolean active  = self.active;

        btweaks$hoverProgress += (hovered ? 1f : -1f) * 0.15f;
        btweaks$hoverProgress = Math.clamp(btweaks$hoverProgress, 0f, 1f);
        float t = btweaks$hoverProgress;

        int bgNormal  = 0xFF1a1a1a;
        int bgHovered = 0xFF2e2e2e;
        int bgInactive = 0xFF170404;
        int bgColor = active ? lerpColor(bgNormal, bgHovered, t) : bgInactive;

        int borderNormal  = 0xFF121212;
        int borderHovered = 0xFF171717;
        int borderInactive = 0xFF0d0202;
        int borderColor = active ? lerpColor(borderNormal, borderHovered, t) : borderInactive;

        r.roundRect(self.getX(), self.getY(), self.getWidth(), self.getHeight(), bgColor, 4, 0);
        r.strokeRounded(self.getX(), self.getY(), self.getWidth(), self.getHeight(), borderColor, 4, 1, 0);


        int textColor = active ? 0xFFFFFFFF : 0xFF888888;
        String text = ((CycleButton)(Object) this).getMessage().getString();
        float fontSize = 10f;

        float[][] quads = getJersey().getQuads(text, 0, 0, fontSize);
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;

        for (float[] q : quads) {
            if (q[1] < minY) minY = q[1];
            if (q[3] > maxY) maxY = q[3];
        }

        float textHeight = maxY - minY;
        float tx = self.getX() + (self.getWidth()  - getJersey().textSize(text, fontSize)) / 2f;
        float ty = self.getY() + (self.getHeight() - textHeight) / 2f - minY;

        r.drawText(getJersey(), ((CycleButton)(Object) this).getMessage().getVisualOrderText(), tx, ty, fontSize, textColor, 1);
        r.flush(graphics);
        ci.cancel();
    }

    @Unique
    private static int lerpColor(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF, aa = (a >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF, ba = (b >> 24) & 0xFF;
        int r = (int)(ar + (br - ar) * t);
        int g = (int)(ag + (bg - ag) * t);
        int bl = (int)(ab + (bb - ab) * t);
        int al = (int)(aa + (ba - aa) * t);
        return (al << 24) | (r << 16) | (g << 8) | bl;
    }
}