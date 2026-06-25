package dev.bsprout.btweaks.client.extras;

import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.mixin.accessor.PlayerTabOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;

import java.util.Collection;

public class BedwarsDetector {
    private static boolean inBedwars = false;
    private int tickCounter = 0;
    private Logger logger = BtweaksClient.getLogger();

    public void runBedwarsCheck(Minecraft client){
        if (client.level == null || client.player == null) {
            return;
        }

        tickCounter++;
        if (tickCounter >= 40) {
            tickCounter = 0;
            inBedwars = checkScoreboard(client) || checkTabList(client);
        }
    }

    private boolean checkScoreboard(Minecraft client){
        if (client.level != null) {
            Scoreboard scoreboard = client.level.getScoreboard();

            Objective sidebarObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

            if (sidebarObjective != null) {
                String title = sidebarObjective.getDisplayName().getString().toLowerCase();
                if (containsBedwars(title)) {
                    return true;
                }

                Collection<PlayerScoreEntry> entries = scoreboard.listPlayerScores(sidebarObjective);
                for (PlayerScoreEntry entry : entries) {
                    String ownerName = entry.owner().toLowerCase();
                    if (ownerName.contains("bedwars") || ownerName.contains("bed wars")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkTabList(Minecraft client) {
        if (client.getConnection() == null) return false;

        PlayerTabOverlayAccessor tabList = (PlayerTabOverlayAccessor) client.gui.getTabList();

        Component header = tabList.btweaks$getHeader();
        Component footer = tabList.btweaks$getFooter();

        if (header != null && containsBedwars(header.getString())) return true;
        if (footer != null && containsBedwars(footer.getString())) return true;

        Collection<PlayerInfo> entries = client.getConnection().getOnlinePlayers();
        for (PlayerInfo entry : entries) {
            if (entry.getTabListDisplayName() != null) {
                if (containsBedwars(entry.getTabListDisplayName().getString())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean containsBedwars(String text) {
        String lower = text.toLowerCase();
        return lower.contains("bed wars") || lower.contains("bedwars");
    }

    public static boolean isInBedwars(){
        return inBedwars;
    }
}
