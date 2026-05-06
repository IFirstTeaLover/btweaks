package dev.bsprout.btweaks.client.mixin;

import dev.bsprout.btweaks.client.RoundButton;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.bsprout.btweaks.client.KeyLogger.configOpened;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void btweaks$addConfigButton(CallbackInfo ci) {

    }
}