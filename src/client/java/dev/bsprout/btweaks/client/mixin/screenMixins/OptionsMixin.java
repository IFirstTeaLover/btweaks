package dev.bsprout.btweaks.client.mixin.screenMixins;

import dev.bsprout.brapi.client.BFont;
import dev.bsprout.brapi.client.BRender;
import dev.bsprout.btweaks.client.mixin.accessor.TooltipTextAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class OptionsMixin {
    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;)V",
            at = @At("HEAD"), cancellable = true)
    private void btweaks$renderTooltip(Font font, List<ClientTooltipComponent> list, int x, int y, ClientTooltipPositioner positioner, @Nullable Identifier identifier, CallbackInfo ci) {
        if (!(Minecraft.getInstance().screen instanceof OptionsSubScreen)) return;

        // measure
        int width = 0, height = list.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent c : list) {
            int compWidth;
            if (c instanceof ClientTextTooltip textComp) {
                FormattedCharSequence line = ((TooltipTextAccessor) textComp).btweaks$getText();
                compWidth = (int) getJersey().textSize(line.toString(), 8f);
            } else {
                compWidth = c.getWidth(font);
            }
            if (compWidth > width) width = compWidth;
            height += c.getHeight(font);
        }

        GuiGraphics self = (GuiGraphics)(Object) this;
        Vector2ic pos = positioner.positionTooltip(self.guiWidth(), self.guiHeight(), x, y, width, height);
        int px = pos.x(), py = pos.y();

        BRender r = new BRender();
        r.roundRect(px - 4, py - 4, width + 8, height + 8, 0xFF0a0a0a, 6, 100);
        r.strokeRounded(px - 4, py - 4, width + 8, height + 8, 0xFF222222, 6, 1, 100);

        int ry = py;
        for (int i = 0; i < list.size(); i++) {
            ClientTooltipComponent comp = list.get(i);
            if (comp instanceof ClientTextTooltip textComp) {
                FormattedCharSequence line = ((TooltipTextAccessor) textComp).btweaks$getText();
                r.drawText(getJersey(), line, px, ry + 12f, 12f, 0xFFFFFFFF, 101);
            } else {
                comp.renderText(self, font, px, ry);
            }
            ry += comp.getHeight(font) + (i == 0 ? 2 : 0);
        }

        r.flush(self);
        ci.cancel();
    }

    @Unique
    private static BFont jersey;

    @Unique
    private static BFont getJersey() {
        if (jersey == null)
            jersey = new BFont(Identifier.fromNamespaceAndPath("btweaks", "font/jersey20.ttf"));
        return jersey;
    }
}