package dev.bsprout.btweaks.client;

import dev.bsprout.brapi.client.BFont;
import dev.bsprout.brapi.client.BTexture;
import net.minecraft.resources.Identifier;

public class AssetManager {
    private static BFont jersey;
    public static final BTexture iron_ingot = new BTexture(Identifier.fromNamespaceAndPath("minecraft", "textures/item/iron_ingot.png"));
    public static final BTexture gold_ingot = new BTexture(Identifier.fromNamespaceAndPath("minecraft", "textures/item/gold_ingot.png"));
    public static final BTexture diamond = new BTexture(Identifier.fromNamespaceAndPath("minecraft", "textures/item/diamond.png"));
    public static final BTexture emerald = new BTexture(Identifier.fromNamespaceAndPath("minecraft", "textures/item/emerald.png"));


    public static BFont getJersey() {
        if (jersey == null)
            jersey = new BFont(Identifier.fromNamespaceAndPath("btweaks", "font/notosans_semibold.ttf"));
        return jersey;
    }
}
