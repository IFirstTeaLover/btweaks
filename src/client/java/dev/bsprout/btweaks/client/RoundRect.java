package dev.bsprout.btweaks.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.bsprout.btweaks.client.mixin.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.Objects;

import static dev.bsprout.btweaks.client.BtweaksClient.LOGGER;

public class RoundRect {
    public static FontDescription inter = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "inter"));
    public static FontDescription gsans = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "gsans"));
    public static FontDescription notoEmoji = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "emoji"));
    public static FontDescription noto = new FontDescription.Resource(Identifier.fromNamespaceAndPath("btweaks", "noto"));
    public static Identifier roundTexture = Identifier.fromNamespaceAndPath("btweaks", "textures/gui/rounded_corners.png");
//    public static final VertexFormatElement RECT_SIZE = VertexFormatElement.register(
//            7, 3, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2
//    );
//    public static final VertexFormatElement RECT_LOC = VertexFormatElement.register(
//            8, 4, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2
//    );
//
//    public static final VertexFormat POSITION_COLOR_RECT = VertexFormat.builder()
//            .add("Position", VertexFormatElement.POSITION)
//            .add("Color",    VertexFormatElement.COLOR)
//            .add("RectSize", RECT_SIZE)
//            .add("RectLoc",  RECT_LOC)
//            .build();
//
//    static final RenderPipeline PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
//            .withLocation("pipeline/rounded_rect")
//            .withVertexFormat(POSITION_COLOR_RECT, VertexFormat.Mode.QUADS)
//            .build();
//
//    private static final RenderType RENDER_TYPE = RenderType.create(
//            "btweaks:rounded_rect",
//            RenderSetup.builder(PIPELINE).createRenderSetup()
//    );
//
//    public static void draw(GuiGraphics graphics, float x, float y, float width, float height, int color) {
//        float a = ((color >> 24) & 0xFF) / 255f;
//        float r = ((color >> 16) & 0xFF) / 255f;
//        float g = ((color >> 8)  & 0xFF) / 255f;
//        float b = (color & 0xFF) / 255f;
//
//        double scale = Minecraft.getInstance().getWindow().getGuiScale();
//        int sh   = Minecraft.getInstance().getWindow().getScreenHeight();
//        int guiW = Minecraft.getInstance().getWindow().getGuiScaledWidth();
//        int guiH = Minecraft.getInstance().getWindow().getGuiScaledHeight();
//
//        float sx  = (float)(x * scale);
//        float sy  = (float)(sh - (y + height) * scale);
//        float sw2 = (float)(width * scale);
//        float sh2 = (float)(height * scale);
//
//        float ndcX1 = (x / guiW) * 2f - 1f;
//        float ndcX2 = ((x + width) / guiW) * 2f - 1f;
//        float ndcY1 = 1f - (y / guiH) * 2f;
//        float ndcY2 = 1f - ((y + height) / guiH) * 2f;
//
//        ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
//        BufferBuilder buf = new BufferBuilder(allocator, VertexFormat.Mode.QUADS, POSITION_COLOR_RECT);
//
//        buf.addVertex(ndcX1, ndcY2, 0).setColor(r,g,b,a).setUv2((int)sw2, (int)sh2).setUv(sx, sy);
//        buf.addVertex(ndcX2, ndcY2, 0).setColor(r,g,b,a).setUv2((int)sw2, (int)sh2).setUv(sx, sy);
//        buf.addVertex(ndcX2, ndcY1, 0).setColor(r,g,b,a).setUv2((int)sw2, (int)sh2).setUv(sx, sy);
//        buf.addVertex(ndcX1, ndcY1, 0).setColor(r,g,b,a).setUv2((int)sw2, (int)sh2).setUv(sx, sy);
//
//        MeshData mesh = buf.buildOrThrow();
//        RENDER_TYPE.draw(mesh);
//        allocator.close();
//    }

//    public static void draw(GuiGraphics graphics, float x, float y, float width, float height, int color) {
//        graphics.fill((int)x, (int)y, (int)(x + width), (int)(y + height), color);
//    }
public static void draw(GuiGraphics graphics, float x, float y, float width, float height, int argb,
                        int tl, int tr, int bl, int br) {
    int texSize = 512;
    int uvSize = 128;

    int ix = (int) x;
    int iy = (int) y;
    int iw = (int) width;
    int ih = (int) height;

    RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;


    // Top-Left
    if (tl > 0) {
        graphics.blit(pipeline, roundTexture, ix, iy, 0, 0, tl, tl, uvSize, uvSize, texSize, texSize, argb);
    }

    // Top-Right
    if (tr > 0) {
        graphics.blit(pipeline, roundTexture, ix + iw - tr, iy, texSize - uvSize, 0, tr, tr, uvSize, uvSize, texSize, texSize, argb);
    }

    // Bottom-Left
    if (bl > 0) {
        graphics.blit(pipeline, roundTexture, ix, iy + ih - bl, 0, texSize - uvSize, bl, bl, uvSize, uvSize, texSize, texSize, argb);
    }

    // Bottom-Right
    if (br > 0) {
        graphics.blit(pipeline, roundTexture, ix + iw - br, iy + ih - br, texSize - uvSize, texSize - uvSize, br, br, uvSize, uvSize, texSize, texSize, argb);
    }

    if (tl + tr + bl + br == 0){graphics.fill(ix, iy, ix + iw, iy + ih, argb); return;}

    // Top & Bottom
    graphics.fill(ix + tl, iy, ix + iw - tr, iy + Math.max(tl, tr), argb);
    graphics.fill(ix + bl, iy + ih - Math.max(bl, br), ix + iw - br, iy + ih, argb);

    // Left & Right
    graphics.fill(ix, iy + tl, ix + tl, iy + ih - bl, argb);
    graphics.fill(ix + iw - tr, iy + tr, ix + iw, iy + ih - br, argb);

    // Center block
    int maxLeft = Math.max(tl, bl);
    int maxRight = Math.max(tr, br);
    graphics.fill(ix + maxLeft, iy + Math.max(tl, tr), ix + iw - maxRight, iy + ih - Math.max(bl, br), argb);

    if (tl == 0) graphics.fill(ix, iy, ix + maxLeft, iy + maxLeft, argb);
    if (tr == 0) graphics.fill(ix + iw - maxRight, iy, ix + iw, iy + maxRight, argb);
    if (bl == 0) graphics.fill(ix, iy + ih - maxLeft, ix + maxLeft, iy + ih, argb);
    if (br == 0) graphics.fill(ix + iw - maxRight, iy + ih - maxRight, ix + iw, iy + ih, argb);
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