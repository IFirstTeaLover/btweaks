package dev.bsprout.btweaks.client.mixin;

import dev.bsprout.brapi.client.BRender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Button.class)
public class ButtonMixin extends AbstractButton{
    public ButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "renderContents", at = @At("HEAD"), cancellable = true)
    private void brapi$renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        BRender r = new BRender();

        boolean hovered = this.isHovered();
        boolean active  = this.active;

        // Background
        int bgColor = active
                ? (hovered ? 0xFF5A5A5A : 0xFF3A3A3A)
                : 0xFF222222;

        r.roundRect(this.getX(), this.getY(), this.width, this.height, bgColor, 4, 0);

        // Border
        int borderColor = active
                ? (hovered ? 0xFFAAAAAA : 0xFF666666)
                : 0xFF444444;

        r.strokeRounded(this.getX(), this.getY(), this.width, this.height, borderColor, 4, 1, 0);

        // Label — use MC font via BRender fallback
        int textColor = active ? 0xFFFFFFFF : 0xFF888888;
        float cx = this.getX() + this.width  / 2f;
        float cy = this.getY() + this.height / 2f - 4; // MC font is 8px tall

        r.drawTextCentered(this.getMessage().getString(), cx, cy, textColor, 1);

        r.flush(graphics);

        ci.cancel();
    }
}
