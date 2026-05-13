package dev.bsprout.btweaks.client.mixin;

import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import dev.bsprout.btweaks.client.mixin.accessor.AccessorDrawingBackgroundGraphicsAccess;
import dev.bsprout.btweaks.client.mixin.accessor.AccessorDrawingFocusedGraphicsAccess;
import io.github.humbleui.skija.Canvas;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatMixin {

    @Shadow private Minecraft minecraft;
    @Shadow public abstract boolean isChatFocused();
    @Shadow private List<GuiMessage.Line> trimmedMessages;

    private boolean drewBackground = false;

    @Inject(method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V",
            at = @At("HEAD"))
    private void resetBg(ChatComponent.ChatGraphicsAccess access, int guiHeight, int ticks, boolean focused, CallbackInfo ci) {
        drewBackground = false;
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"))
    private void redirectFill(ChatComponent.ChatGraphicsAccess instance, int x1, int y1, int x2, int y2, int color) {
        Canvas canvas = BtweaksClient.canvas;
        if (canvas == null) {
            return;
        }
        if (!drewBackground) {
            drewBackground = true;

            GuiGraphics guiGraphics = null;
            if (instance instanceof AccessorDrawingBackgroundGraphicsAccess a) {
                guiGraphics = a.getGraphics();
            } else if (instance instanceof AccessorDrawingFocusedGraphicsAccess a) {
                guiGraphics = a.getGraphics();
            }

            if (guiGraphics != null) {
                double scale = this.minecraft.options.chatScale().get();
                int uiScale = minecraft.getWindow().getGuiScale();
                int rounding = (int) Math.round(uiScale * 1.5);

                int linesPerPage = this.getLinesPerPage();
                int lineHeight = this.getLineHeight();
                int totalH = (int)(linesPerPage * lineHeight * scale);
                int totalW = (int)((x2 - x1) * scale);

                int screenX = (int)(x1 * scale) + 4;
                int screenY = guiGraphics.guiHeight() - 40 - totalH;

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().identity();

                RoundRect.draw(canvas, screenX, screenY, totalW, totalH,
                        WidgetGeneral.getGlobalWidgetColor(), rounding);

                guiGraphics.pose().popMatrix();
            }
        }
    }

    @Shadow public abstract int getLinesPerPage();
    @Shadow private int getLineHeight() { return 0; }
}