package dev.bsprout.btweaks.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.Objects;

import static dev.bsprout.btweaks.client.BtweaksClient.LOGGER;

import dev.bsprout.brapi.client.BRender;

public class RoundRect {
    public static final BRender bRender = new BRender();
    public static FontDescription inter = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "inter"));
    public static FontDescription gsans = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "gsans"));
    public static FontDescription notoEmoji = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "emoji"));
    public static FontDescription noto = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "noto"));
    public static Identifier roundTexture = Identifier.fromNamespaceAndPath("btweaks", "textures/gui/rounded_corners.png");

//lets pray this works
public static void draw(GuiGraphics graphics, float x, float y, float width, float height, int argb,
                        int tl, int tr, int bl, int br) {
    int texSize = 512;
    int uvSize = 128;

    int ix = (int) x;
    int iy = (int) y;
    int iw = (int) width;
    int ih = (int) height;

    RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;

    int leftW = Math.max(tl, bl);
    int rightW = Math.max(tr, br);
    int topH = Math.max(tl, tr);
    int bottomH = Math.max(bl, br);

    if (tl > 0) graphics.blit(pipeline, roundTexture, ix, iy, 0, 0, tl, tl, uvSize, uvSize, texSize, texSize, argb);
    if (tr > 0) graphics.blit(pipeline, roundTexture, ix + iw - tr, iy, texSize - uvSize, 0, tr, tr, uvSize, uvSize, texSize, texSize, argb);
    if (bl > 0) graphics.blit(pipeline, roundTexture, ix, iy + ih - bl, 0, texSize - uvSize, bl, bl, uvSize, uvSize, texSize, texSize, argb);
    if (br > 0) graphics.blit(pipeline, roundTexture, ix + iw - br, iy + ih - br, texSize - uvSize, texSize - uvSize, br, br, uvSize, uvSize, texSize, texSize, argb);

    graphics.fill(ix + tl, iy, ix + iw - tr, iy + topH, argb);
    graphics.fill(ix + bl, iy + ih - bottomH, ix + iw - br, iy + ih, argb);
    graphics.fill(ix, iy + tl, ix + leftW, iy + ih - bl, argb);
    graphics.fill(ix + iw - rightW, iy + tr, ix + iw, iy + ih - br, argb);

    graphics.fill(ix + leftW, iy + topH, ix + iw - rightW, iy + ih - bottomH, argb);

    if (tl == 0) graphics.fill(ix, iy, ix + leftW, iy + topH, argb);
    if (tr == 0) graphics.fill(ix + iw - rightW, iy, ix + iw, iy + topH, argb);
    if (bl == 0) graphics.fill(ix, iy + ih - bottomH, ix + leftW, iy + ih, argb);
    if (br == 0) graphics.fill(ix + iw - rightW, iy + ih - bottomH, ix + iw, iy + ih, argb);
}

    public static void draw(GuiGraphics graphics, float x, float y, float width, float height, int argb) {
        draw(graphics, x, y, width, height, argb, 0, 0, 0, 0);
    }

    public static void draw(GuiGraphics graphics, float x, float y, float width, float height, int argb, int cornerSize) {
        draw(graphics, x, y, width, height, argb, cornerSize, cornerSize, cornerSize, cornerSize);
    }

    public static void drawText(GuiGraphics graphics, String text, float x, float y, float width, float height, int textColor, String fontType, String align) {
        var font = Minecraft.getInstance().font;
        FontDescription fontId = switch (fontType) {
            case "gsans" -> gsans;
            case "inter" -> inter;
            case "noto" -> noto;
            case "emoji" -> notoEmoji;
            default -> null;
        };
        MutableComponent component = Component.literal(text);

        if (fontId != null) {
            component = component.withStyle(s -> s.withFont(fontId));
        } else if (!fontType.equals("default")) {
            LOGGER.warn("[btweaks] Unknown font! Falling back to inter.");
            component = component.withStyle(s -> s.withFont(inter));
        }

        int textWidth = font.width(component);

        int centerX = (int)(x + (width / 2) - ((float) textWidth / 2f));
        int centerY = (int)(y + (height / 2) - ((float) font.lineHeight / 2f));

        if (Objects.equals(align, "center")) {
            graphics.drawString(font, component, centerX, centerY, textColor, false);
        }else if (Objects.equals(align, "left")){
            graphics.drawString(font, component, (int) x, centerY, textColor, false);
        }else if (Objects.equals(align, "right")){
            graphics.drawString(font, component, (int) x - textWidth, centerY, textColor, false);
        }

    }

    public static void drawText(GuiGraphics graphics, String text, float x, float y, float width, float height, int textColor) {
        drawText(graphics, text, x, y, width, height, textColor, "default", "center");
    }

    public static void drawImage(GuiGraphics graphics, Identifier texture, int x, int y, int width, int height) {
        RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;
        int uvSize = 128;
        int texSize = 128;

        graphics.blit(pipeline, texture, x, y, 0, 0, width, height, uvSize, uvSize, texSize, texSize, 0xFFFFFFFF);
    }
}