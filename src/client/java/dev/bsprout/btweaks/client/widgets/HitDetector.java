package dev.bsprout.btweaks.client.widgets;

import dev.bsprout.btweaks.client.RoundRect;
import dev.bsprout.btweaks.client.Widget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static dev.bsprout.btweaks.client.BtweaksClient.LOGGER;
import static dev.bsprout.btweaks.client.BtweaksClient.mc;

public class HitDetector implements Widget {
    String text = null;
    @Override
    public void render(GuiGraphics ctx, int uiScale, float x, float y, DeltaTracker tick) {
        text = null;
        var from = mc.player.getEyePosition();
        var to   = from.add(mc.player.getViewVector(1.0f).scale(10));
        AttackRange attackRange = mc.player.entityAttackRange();

        Entity closest = null;
        double closestDist = 10;
        for (Entity e : mc.level.getEntities(mc.player, mc.player.getBoundingBox().expandTowards(mc.player.getViewVector(1.0f).scale(10)).inflate(1.0))) {
            if (!(e instanceof Mob) && !(e instanceof Player)) return;
            var eBox = e.getBoundingBox().inflate(e.getPickRadius());
            var result = eBox.clip(from, to);
            if (result.isPresent()) {
                double d = from.distanceTo(result.get());
                if (d < closestDist) {
                    closestDist = d;
                    closest = e;
                }
            }
        }

        if (closest != null) {
            boolean canHit = attackRange.isInRange(mc.player, closest.getBoundingBox(), 0.0);
            text = canHit ? "§aCan hit" : "§cCan't hit";
        }

        if (text == null) return;
        int height = uiScale * 5;
        int padding = uiScale * 2;
        int textWidth = mc.font.width(text);
        float rectWidth = textWidth + padding * 2;

        RoundRect.draw(ctx, x, y, rectWidth, height, 0x80262626, 3);
        RoundRect.drawText(ctx, text, x, y, rectWidth, height, 0xFFFFFFFF);
    }

    @Override
    public float getWidth(int uiScale) {
        if (text == null) return 0;
        return mc.font.width(text) + uiScale * 4;
    }

    @Override
    public float getHeight(int uiScale) {
        return uiScale * 5;
    }
}
