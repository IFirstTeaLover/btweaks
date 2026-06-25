package dev.bsprout.btweaks.client.mixin.buttonMixins;

import dev.bsprout.brapi.client.BRender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.bsprout.btweaks.client.AssetManager.getJersey;

@Mixin(AbstractSliderButton.class)
public class SliderButtonMixin {
    @Shadow
    protected double value;

    @Unique
    private float btweaks$hoverProgress = 0f;

    @Unique
    private static final BRender r = new BRender();

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void btweaks$sliderRenderer(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci){
        AbstractWidget self = (AbstractWidget)(Object) this;
        AbstractSliderButton slider = (AbstractSliderButton)(Object) this;

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

        // fill
        int handleX = (int)(value * (self.getWidth() - 8));
        int fillWidth = handleX + 4; // center of handle

        int round = 4;
        if (fillWidth < round)  round = fillWidth;
        r.roundRect(self.getX()+1, self.getY()+1, fillWidth-2, self.getHeight()-2, 0xFF2a2a2a, round, 2);

        // handle
        r.roundRect(self.getX() + handleX, self.getY(), 8, self.getHeight(), 0xFF3a3a3a, 4, 3);
        r.strokeRounded(self.getX() + handleX, self.getY(), 8, self.getHeight(), 0xFF2a2a2a, 4, 1, 3);

        String text = self.getMessage().getString().replaceAll("§[0-9a-fk-or]", "");
        float fontSize = 10f;
        float textWidth = getJersey().textSize(text, fontSize);
        float[][] quads = getJersey().getQuads(text, 0, 0, fontSize);
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        for (float[] q : quads) {
            if (q[1] < minY) minY = q[1];
            if (q[3] > maxY) maxY = q[3];
        }
        float textHeight = maxY - minY;
        float tx = self.getX() + (self.getWidth() - textWidth) / 2f;
        float ty = self.getY() + (self.getHeight() - textHeight) / 2f - minY;
        r.drawText(getJersey(), self.getMessage().getVisualOrderText(), tx, ty, fontSize, active ? 0xFFFFFFFF : 0xFF888888, 4);

        r.flush(guiGraphics);
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
