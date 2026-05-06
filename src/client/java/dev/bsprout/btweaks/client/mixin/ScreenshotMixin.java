package dev.bsprout.btweaks.client.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

@Mixin(Screenshot.class)
public class ScreenshotMixin {
    @ModifyVariable(method = "grab(Ljava/io/File;Ljava/lang/String;Lcom/mojang/blaze3d/pipeline/RenderTarget;ILjava/util/function/Consumer;)V",
            at = @At("HEAD"), argsOnly = true)
    private static Consumer<Component> btweaks$wrapScreenshotConsumer(Consumer<Component> original) {
        return component -> {
            if (component instanceof MutableComponent mc && mc.getContents() instanceof TranslatableContents translatable) {
                Object[] args = translatable.getArgs();
                if (args.length > 0 && args[0] instanceof MutableComponent fileComp) {
                    if (fileComp.getStyle().getClickEvent() instanceof net.minecraft.network.chat.ClickEvent.OpenFile openFile) {
                        String pathWithTag = openFile.file().getAbsolutePath() + "#is_btweaks_copy";

                        MutableComponent copyButton = Component.literal(" [")
                                .append(Component.translatable("chat.copy"))
                                .append("]")
                                .withStyle(style -> style
                                        .withColor(ChatFormatting.GREEN)
                                        .withClickEvent(new ClickEvent.CopyToClipboard(pathWithTag))
                                        .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to copy to clipboard")))
                                );
                        mc.append(copyButton);
                    }
                }
            }
            original.accept(component);
        };
    }
}