package dev.bsprout.btweaks.client.mixin.buttonMixins;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.mixin.accessor.ImageButtonAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImageButton.class)
@Environment(EnvType.CLIENT)
public abstract class ImageButtonMixin {
    @Shadow
    protected WidgetSprites sprites;
    @Unique
    private float btweaks$hoverProgress = 0f;

    @Unique
    private static final BRender r = new BRender();

    @Inject(method = "renderContents", at = @At("HEAD"), cancellable = true)
    private void btweaks$renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        AbstractWidget self = (AbstractWidget)(Object) this;
        ImageButton ib = (ImageButton)(Object) this;

        boolean hovered = self.isHovered();
        boolean active  = self.active;

        btweaks$hoverProgress += (hovered ? 0.2f : -0.1f);
        btweaks$hoverProgress = Math.clamp(btweaks$hoverProgress, 0f, 1f);
        float t = btweaks$hoverProgress;

        int bgColor     = active ? lerpColor(0xFF1a1a1a, 0xFF2e2e2e, t) : 0xFF121212;
        int borderColor = active ? lerpColor(0xFF222222, 0xFF2e2e2e, t) : 0xFF181818;

        r.roundRect(self.getX(), self.getY(), self.getWidth(), self.getHeight(), bgColor, 4, 0);
        r.strokeRounded(self.getX(), self.getY(), self.getWidth(), self.getHeight(), borderColor, 4, 1, 0);


        WidgetSprites sprites = ((ImageButtonAccessor)(Object) this).btweaks$getSprites();

        Identifier identifier = sprites.get(self.isActive(), self.isHoveredOrFocused());
        //graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, self.getX(), self.getY(), self.getWidth(), self.getHeight());

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
