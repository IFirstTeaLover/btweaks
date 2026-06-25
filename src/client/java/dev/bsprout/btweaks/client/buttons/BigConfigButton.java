package dev.bsprout.btweaks.client.buttons;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.brapi.client.BTexture;
import dev.bsprout.brapi.client.NineSlice;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class BigConfigButton extends AbstractButton {
    private Runnable onClick;
    BRender bRender = new BRender();
    private final NineSlice texture;

    public BigConfigButton(int x, int y, int width, int height, Component text, Runnable onClick, NineSlice texture) {
        super(x, y, width, height, text);
        this.onClick = onClick;
        this.texture = texture;
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        if (this.onClick != null) {
            this.onClick.run();
        }
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean isSelected = this.isHoveredOrFocused();
        int tintColor = isSelected ? 0xFFF5F5F5 : 0xFFFFFFFF;

        bRender.drawTexture9Slice(
                this.texture,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                tintColor,
                false,
                5
        );

        bRender.flush(guiGraphics);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
