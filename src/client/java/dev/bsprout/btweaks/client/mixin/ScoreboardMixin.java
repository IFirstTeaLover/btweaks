package dev.bsprout.btweaks.client.mixin;

import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.config.ConfigWindow;
import dev.bsprout.btweaks.client.helpers.DisplayEntry;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import io.github.humbleui.skija.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

import static dev.bsprout.btweaks.client.config.ConfigWindow.globalDelta;

@Mixin(Gui.class)
public abstract class ScoreboardMixin {
    private float animWidth = -1;
    private float animHeight = -1;
    float animX = -1;
    float animY = -1;

    @Shadow private Minecraft minecraft;
    @Shadow public abstract Font getFont();

    private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value)
            .reversed()
            .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);


    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void displayScoreboardSidebar(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        ci.cancel();
        Canvas canvas = BtweaksClient.canvas;
        if (canvas == null) {
            return;
        }

        int uiScale = minecraft.getWindow().getGuiScale();
        int rounding = (int) Math.round(uiScale * 1.5);

        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat numberFormat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);

        DisplayEntry[] lvs = scoreboard.listPlayerScores(objective).stream()
                .filter(e -> !e.isHidden())
                .sorted(SCORE_DISPLAY_ORDER)
                .limit(15L)
                .map(e -> {
                    PlayerTeam team = scoreboard.getPlayersTeam(e.owner());
                    Component name = PlayerTeam.formatNameForTeam(team, e.ownerName());
                    Component score = e.formatValue(numberFormat);
                    return new DisplayEntry(name, score, this.getFont().width(score));
                })
                .toArray(DisplayEntry[]::new);

        Component title = objective.getDisplayName();
        int titleWidth = this.getFont().width(title);
        int maxWidth = titleWidth;
        int colonWidth = this.getFont().width(": ");

        for (DisplayEntry e : lvs)
            maxWidth = Math.max(maxWidth, this.getFont().width(e.name()) + (e.scoreWidth() > 0 ? colonWidth + e.scoreWidth() : 0));

        int m = lvs.length;
        int o = guiGraphics.guiHeight() / 2 + (m * 9) / 3;
        int q = guiGraphics.guiWidth() - maxWidth - 3;
        int r = guiGraphics.guiWidth() - 3 + 2;
        int u = o - m * 9;

        float targetX      = q - 2;
        float targetY      = u - 9 - 1;
        float targetHeight = m * 9 + 1;

        if (animX < 0) { animX = targetX; animY = targetY; animHeight = targetHeight; }

        float speed = Math.min(1.0f, Math.max(0f, (float)(0.2 * ConfigWindow.globalDelta * 2)));
        animX      += (targetX      - animX)      * speed;
        animY      += (targetY      - animY)      * speed;
        animHeight += (targetHeight - animHeight) * speed;

        int fixedWidth = r - (int) animX + 2;

        float xOffset = animX - targetX;

        int color = WidgetGeneral.getGlobalWidgetColor();

        RoundRect.draw(canvas, animX - uiScale, animY,          fixedWidth, 9,           color, rounding, rounding, 0, 0);
        RoundRect.draw(canvas, animX - uiScale, animY + 9,      fixedWidth, animHeight,  color, 0, 0, rounding, rounding);

        guiGraphics.drawString(this.getFont(), title, (int)(q + maxWidth / 2 - titleWidth / 2 + xOffset), u - 9, -1, false);

        for (int v = 0; v < m; v++) {
            DisplayEntry e = lvs[v];
            int w = o - (m - v) * 9;
            guiGraphics.drawString(this.getFont(), e.name(), (int)(q + xOffset - uiScale), w, -1, false);
            guiGraphics.drawString(this.getFont(), e.score(), (int)(r - e.scoreWidth() + xOffset - uiScale), w, -1, false);
        }
    }
}
