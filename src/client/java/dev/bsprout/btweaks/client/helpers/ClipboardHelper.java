package dev.bsprout.btweaks.client.helpers;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class ClipboardHelper {
    public static void copyImageToClipboard(File file) {
        System.setProperty("java.awt.headless", "false");

        try {
            BufferedImage image = ImageIO.read(file);
            Transferable transferable = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.imageFlavor};
                }
                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.imageFlavor.equals(flavor);
                }
                @Override
                public Object getTransferData(DataFlavor flavor) {
                    return image;
                }
            };

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}