package dev.bsprout.btweaks.client;

import dev.bsprout.brapi.client.BRender;
import dev.bsprout.brapi.client.NineSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class BigSelectableIconButton extends AbstractButton {
    private final Runnable onClick;
    public final BRender bRender = new BRender();
    private final NineSlice nineSliceTextures;

    private final WidgetSprites sprite;
    private final int spriteWidth;
    private final int spriteHeight;

    public BigSelectableIconButton(int x, int y, int width, int height, Component message, Runnable onClick, NineSlice nineSliceTextures, WidgetSprites sprite, int spriteWidth, int spriteHeight) {
        super(x, y, width, height, message);
        this.onClick = onClick;
        this.nineSliceTextures = nineSliceTextures;
        this.sprite = sprite;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
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

        int iconX = this.getX() + (this.getWidth() / 2) - (this.spriteWidth / 2);
        int iconY = this.getY() + (this.getHeight() / 2) - (this.spriteHeight / 2);

        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                this.sprite.get(this.isActive(), isSelected),
                iconX,
                iconY,
                this.spriteWidth,
                this.spriteHeight,
                this.alpha
        );

        bRender.flush(guiGraphics);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}