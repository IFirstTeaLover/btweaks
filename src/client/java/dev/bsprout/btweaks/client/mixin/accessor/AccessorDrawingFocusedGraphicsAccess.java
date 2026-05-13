package dev.bsprout.btweaks.client.mixin.accessor;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess")
public interface AccessorDrawingFocusedGraphicsAccess {
    @Accessor("graphics")
    GuiGraphics getGraphics();
}