package dev.bsprout.btweaks.client.mixin;

import dev.bsprout.btweaks.client.helpers.PingTracker;
import net.minecraft.client.multiplayer.PingDebugMonitor;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PingDebugMonitor.class)
public class PingMixin {
    @Inject(method = "onPongReceived", at = @At("HEAD"))
    private void onPongReceived(ClientboundPongResponsePacket packet, CallbackInfo ci) {
        long latency = Util.getMillis() - packet.time();
        PingTracker.setLivePing((int) latency);
    }
}