package dev.bsprout.btweaks.client.mixin;

import com.mojang.authlib.GameProfile;
import dev.bsprout.btweaks.client.BtweaksClient;
import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.helpers.TabEntry;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import io.github.humbleui.skija.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(PlayerTabOverlay.class)
public abstract class TabOverlayMixin {
    @Shadow private Minecraft minecraft;
    @Shadow private Component header;
    @Shadow private Component footer;
    @Shadow protected abstract Component getNameForDisplay(PlayerInfo playerInfo);
    @Shadow private List<PlayerInfo> getPlayerInfos() { return null; }
    @Shadow protected abstract void renderPingIcon(GuiGraphics guiGraphics, int i, int j, int k, PlayerInfo playerInfo);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(GuiGraphics guiGraphics, int i, Scoreboard scoreboard, Objective objective, CallbackInfo ci) {
        ci.cancel();
        Canvas canvas = BtweaksClient.canvas;
        if (canvas == null) {
            return;
        }
        int w = this.minecraft.options.getBackgroundColor(553648127);
        List<PlayerInfo> list = this.getPlayerInfos();
        List<TabEntry> list2 = new ArrayList<>(list.size());
        int j = this.minecraft.font.width(" ");
        int k = 0;
        int l = 0;

        for (PlayerInfo playerInfo : list) {
            Component component = this.getNameForDisplay(playerInfo);
            k = Math.max(k, this.minecraft.font.width(component));
            int m = 0;
            Component component2 = null;
            int n = 0;
            if (objective != null) {
                ScoreHolder scoreHolder = ScoreHolder.fromGameProfile(playerInfo.getProfile());
                ReadOnlyScoreInfo readOnlyScoreInfo = scoreboard.getPlayerScoreInfo(scoreHolder, objective);
                if (readOnlyScoreInfo != null) m = readOnlyScoreInfo.value();

                if (objective.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
                    NumberFormat numberFormat = objective.numberFormatOrDefault(StyledFormat.PLAYER_LIST_DEFAULT);
                    component2 = ReadOnlyScoreInfo.safeFormatValue(readOnlyScoreInfo, numberFormat);
                    n = this.minecraft.font.width(component2);
                    l = Math.max(l, n > 0 ? j + n : 0);
                }
            }
            list2.add(new TabEntry(component, m, component2, n));
        }

        int o = list.size();
        int p = o;
        int q;
        for (q = 1; p > 20; p = (o + q - 1) / q) q++;

        boolean bl = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
        int r = objective == null ? 0 : (objective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS ? 90 : l);

        int n = Math.min(q * ((bl ? 9 : 0) + k + r + 13), i - 50) / q;
        int s = i / 2 - (n * q + (q - 1) * 5) / 2;
        int t = 10;
        int u = n * q + (q - 1) * 5;

        List<FormattedCharSequence> list3 = null;
        if (this.header != null) {
            list3 = this.minecraft.font.split(this.header, i - 50);
            for (FormattedCharSequence seq : list3) u = Math.max(u, this.minecraft.font.width(seq));
        }

        List<FormattedCharSequence> list4 = null;
        if (this.footer != null) {
            list4 = this.minecraft.font.split(this.footer, i - 50);
            for (FormattedCharSequence seq : list4) u = Math.max(u, this.minecraft.font.width(seq));
        }

        if (list3 != null) {
            for (FormattedCharSequence seq : list3) {
                int v = this.minecraft.font.width(seq);
                guiGraphics.drawString(this.minecraft.font, seq, i / 2 - v / 2, t, -1);
                t += 9;
            }
            t++;
        }

        int totalTop = t - 1;
        if (list3 != null) totalTop = 10 - 1;

        int totalBottom = t + p * 9;

        if (list4 != null) {
            int footerT = t + p * 9 + 1;
            totalBottom = footerT + list4.size() * 9;
        }

        int rx = i / 2 - u / 2 - 1;
        int rw = (i / 2 + u / 2 + 1) - rx;
        int color = w;
        int uiScale = minecraft.getWindow().getGuiScale();
        int rounding = (int) Math.round(uiScale * 1.5);

        RoundRect.draw(canvas, rx, totalTop, rw, totalBottom - totalTop, color, rounding, rounding, rounding, rounding);
        for (int x = 0; x < o; x++) {
            int v = x / p;
            int y = x % p;
            int z = s + v * n + v * 5;
            int aa = t + y * 9;
            RoundRect.draw(canvas, z, aa, n, 8, color, uiScale);
            if (x < list.size()) {
                PlayerInfo playerInfo2 = list.get(x);
                TabEntry entry = list2.get(x);
                GameProfile gameProfile = playerInfo2.getProfile();
                if (bl) {
                    Player player = this.minecraft.level.getPlayerByUUID(gameProfile.id());
                    boolean bl2 = player != null && AvatarRenderer.isPlayerUpsideDown(player);
                    PlayerFaceRenderer.draw(guiGraphics, playerInfo2.getSkin().body().texturePath(), z, aa, 8, playerInfo2.showHat(), bl2, -1);
                    z += 9;
                }

                guiGraphics.drawString(this.minecraft.font, entry.name(), z, aa, playerInfo2.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
                if (objective != null && playerInfo2.getGameMode() != GameType.SPECTATOR) {
                    int ab = z + k + 1;
                    int ac = ab + r;
                    if (ac - ab > 5 && entry.formattedScore() != null) {
                        guiGraphics.drawString(this.minecraft.font, entry.formattedScore(), ac - entry.scoreWidth(), aa, -1);
                    }
                }
                this.renderPingIcon(guiGraphics, n, z - (bl ? 9 : 0), aa, playerInfo2);
            }
        }

        if (list4 != null) {
            t += p * 9 + 1;
            for (FormattedCharSequence seq : list4) {
                int y = this.minecraft.font.width(seq);
                guiGraphics.drawString(this.minecraft.font, seq, i / 2 - y / 2, t, -1);
                t += 9;
            }
        }
    }
}