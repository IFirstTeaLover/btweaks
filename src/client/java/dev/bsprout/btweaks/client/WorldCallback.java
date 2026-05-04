package dev.bsprout.btweaks.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;

public class WorldCallback {
    public static void register() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            for (Entity entity : world.entitiesForRendering()) {
                if (entity instanceof PrimedTnt tnt) {
                    float seconds = tnt.getFuse() / 20f;

                    ChatFormatting color = ChatFormatting.AQUA;

                    if (seconds < 1){color = ChatFormatting.RED;}
                    else if (seconds < 2){color = ChatFormatting.GOLD;}
                    else if (seconds < 3){color = ChatFormatting.YELLOW;}
                    else if (seconds < 4){color = ChatFormatting.GREEN;}

                    Component text = Component.literal(String.format("%.1fs", seconds))
                            .withStyle(color);

                    tnt.setCustomName(text);
                    tnt.setCustomNameVisible(true);
                }
            }
        });
    }
}