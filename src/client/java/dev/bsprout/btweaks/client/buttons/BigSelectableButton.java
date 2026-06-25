package dev.bsprout.btweaks.client.buttons;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.brapi.client.NineSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class BigSelectableButton extends AbstractButton {
    private Runnable onClick;
    BRender bRender = new BRender();
    private final NineSlice nineSliceTextures;

    public BigSelectableButton(int x, int y, int width, int height, Component message, Runnable onClick, NineSlice nineSliceTextures) {
        super(x, y, width, height, message);
        this.onClick = onClick;
        this.nineSliceTextures = nineSliceTextures;
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        if (this.onClick != null) {
            if (!this.isFocused()) {
                this.setFocused(true);
                return;
            }

            this.onClick.run();
        }
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean isSelected = this.isHoveredOrFocused();
        int tintColor = isSelected ? 0xFF66FF00 : 0xFFFFFFFF;

        bRender.drawTexture9Slice(
                this.nineSliceTextures,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                tintColor,
                false,
                1
        );

        int textX = this.getX() + (this.getWidth() / 2);
        int textY = this.getY() + (this.getHeight() / 2) - (Minecraft.getInstance().font.lineHeight / 2);

        int textColor = isSelected ? 0xFFFFFF : 0x888888;

        if (this.getWidth() > 30) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), textX, textY, textColor);
        }
        bRender.flush(guiGraphics);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
