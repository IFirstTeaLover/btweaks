package dev.bsprout.btweaks.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class RoundButton extends AbstractButton {
    private int radius1;
    private int radius2;
    private int radius3;
    private int radius4;
    private int color;
    private boolean emoji;
    private int textColor = 0xFFFFFFFF;
    private final Runnable onClick;
    private float hoverProgress;
    private int transparency;

    public RoundButton(int x, int y, int width, int height, Component message, int radius1, int radius2, int radius3, int radius4, Runnable onClick, int color, boolean emoji) {
        super(x, y, width, height, message);
        this.onClick = onClick;
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.radius3 = radius3;
        this.radius4 = radius4;
        this.color = color;
        this.emoji = emoji;
    }

    public void setRadius(int radius1, int radius2, int radius3, int radius4) {
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.radius3 = radius3;
        this.radius4 = radius4;
    }

    public void setColor(int color){
        this.color = color;
    }
    public int getColor(){return this.color;}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        if (this.onClick != null) {
            this.onClick.run();
        }
    }

    public void setTextColor(int color){this.textColor = color;}

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.isHovered();
        float animationSpeed = 0.15f;

        if (hovered) {
            hoverProgress = Math.min(1.0f, hoverProgress + animationSpeed * partialTick);
        } else {
            hoverProgress = Math.max(0.0f, hoverProgress - animationSpeed * partialTick);
        }

        int br = (color >> 16) & 0xFF;
        int bg = (color >> 8) & 0xFF;
        int bb = color & 0xFF;

        //brighten
        int er = Math.min(255, br + 20);
        int eg = Math.min(255, bg + 20);
        int eb = Math.min(255, bb + 20);

        int r = (int) (br + (er - br) * hoverProgress);
        int g = (int) (bg + (eg - bg) * hoverProgress);
        int b = (int) (bb + (eb - bb) * hoverProgress);

        int color = (255 << 24) | (r << 16) | (g << 8) | b;

        String activeFont = this.emoji ? "emoji" : "gsans";

        RoundRect.draw(guiGraphics, getX(), getY(), getWidth(), getHeight(), color, this.radius1, this.radius2, this.radius3, this.radius4);
        RoundRect.drawText(guiGraphics, getMessage().getString(), getX(), getY(), getWidth(), getHeight(), textColor, activeFont, "center");
    }

    private int brighten(int color, int amount) {
        int r = Math.min(255, ((color >> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);
        int a = (color >> 24) & 0xFF;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public void playDownSound(SoundManager handler) {
        // silence button
    }

    public void applyTransparency(int transparency){
        this.transparency = transparency;
    }
}