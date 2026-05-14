package dev.bsprout.btweaks.client;

import io.github.humbleui.skija.*;
import net.minecraft.client.Minecraft;

import java.io.FileOutputStream;
import java.io.File;
public class debugDump{
    public static void dump(Surface gpuSurface, String name) {
        if (gpuSurface == null) return;

        int w = gpuSurface.getWidth();
        int h = gpuSurface.getHeight();

        // 1. Create a Bitmap to hold the pixel data
        try (Bitmap bitmap = new Bitmap()) {
            bitmap.allocN32Pixels(w, h, false);

            // 2. Pull the pixels from the GPU (Intel Driver) to the Bitmap (RAM)
            boolean success = gpuSurface.readPixels(bitmap, 0, 0);

            if (!success) {
                System.err.println("[btweaks] Intel Driver blocked the pixel read!");
                return;
            }

            // 3. Save the Bitmap to a PNG
            try (Image img = Image.makeFromBitmap(bitmap)) {
                try (Data data = img.encodeToData(EncodedImageFormat.PNG, 100)) {
                    File file = new File(Minecraft.getInstance().gameDirectory, "dump_" + name + ".png");
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(data.getBytes());
                        System.out.println("[btweaks] SUCCESS: Saved to " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}