package dev.bsprout.btweaks.client.mixin;

import dev.bsprout.brapi.client.BTexture;
import dev.bsprout.brapi.client.NineSlice;
import dev.bsprout.btweaks.client.buttons.BigSelectableButton;
import dev.bsprout.btweaks.client.buttons.BigSelectableIconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.bsprout.brapi.client.BUtils.nineslicify;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    private AbstractWidget[] carouselButtons;
    private final int[] targetSizes = new int[5];
    private final int[] targetXPositions = new int[5];

    private final double[] currentSizes = new double[5];
    private final double[] currentXPositions = new double[5];

    private static int focusedIndex = 2;

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void customTitleScreenInit(CallbackInfo ci) {
        this.clearWidgets();

        BTexture cardTexture = new BTexture(Identifier.fromNamespaceAndPath("brapi", "textures/mc_button.png"));
        NineSlice buttonNS = nineslicify(cardTexture, 4, 4, 4, 4);

        AbstractWidget playButton = new BigSelectableButton(0, 0, 20, 20, Component.literal("Play"), () -> {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }, buttonNS);

        AbstractWidget settingsButton = new BigSelectableButton(0, 0, 20, 20, Component.translatable("menu.options"), () -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }, buttonNS);

        AbstractWidget exitButton = new BigSelectableButton(0, 0, 20, 20, Component.translatable("menu.quit"), () -> {
            this.minecraft.stop();
        }, buttonNS);

        WidgetSprites languageSprites = new WidgetSprites(
                Identifier.fromNamespaceAndPath("minecraft", "widget/language_button"),
                Identifier.fromNamespaceAndPath("minecraft", "widget/language_button_highlighted")
        );

        WidgetSprites accessibilitySprites = new WidgetSprites(
                Identifier.fromNamespaceAndPath("minecraft", "widget/accessibility_button"),
                Identifier.fromNamespaceAndPath("minecraft", "widget/accessibility_button_highlighted")
        );

        AbstractWidget langButton = new BigSelectableIconButton(0, 0, 20, 20, Component.literal(""), () -> {
            this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
        }, buttonNS, languageSprites, 20, 20);

        AbstractWidget accessButton = new BigSelectableIconButton(0, 0, 20, 20, Component.literal(""), () -> {
            this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
        }, buttonNS, accessibilitySprites, 20, 20);

        this.addRenderableWidget(playButton);
        this.addRenderableWidget(settingsButton);
        this.addRenderableWidget(exitButton);
        this.addRenderableWidget(langButton);
        this.addRenderableWidget(accessButton);

        this.carouselButtons = new AbstractWidget[]{ langButton, settingsButton, playButton, exitButton, accessButton };

        this.recalculateCarousel();

        for (int i = 0; i < 5; i++) {
            this.currentSizes[i] = this.targetSizes[i];
            this.currentXPositions[i] = this.targetXPositions[i];

            AbstractWidget btn = this.carouselButtons[i];
            int initialWidth = this.targetSizes[i];
            btn.setWidth(initialWidth);
            btn.setHeight(initialWidth);
            btn.setX(this.targetXPositions[i]);
            btn.setY((this.height / 2) - (initialWidth / 2));
            btn.setFocused(false);
        }

        if (this.carouselButtons[focusedIndex] != null) {
            AbstractWidget targetBtn = this.carouselButtons[focusedIndex];
            targetBtn.setFocused(true);
            this.setFocused(targetBtn);
        }

        ci.cancel();
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.key() == GLFW.GLFW_KEY_LEFT) {
            if (focusedIndex > 0) {
                focusedIndex--;
                this.updateButtonLayouts();
                return true;
            }
        }

        if (keyEvent.key() == GLFW.GLFW_KEY_RIGHT) {
            if (focusedIndex < 4) {
                focusedIndex++;
                this.updateButtonLayouts();
                return true;
            }
        }

        return super.keyPressed(keyEvent);
    }

    private void updateButtonLayouts() {
        this.recalculateCarousel();

        for (int i = 0; i < 5; i++) {
            AbstractWidget btn = this.carouselButtons[i];
            boolean isCurrentFocus = (i == focusedIndex);

            btn.setFocused(isCurrentFocus);
            if (isCurrentFocus) {
                this.setFocused(btn);
            }
        }
    }

    private void recalculateCarousel() {
        int gap = 8;
        int screenCenterX = this.width / 2;

        for (int i = 0; i < 5; i++) {
            int distanceFromFocus = Math.abs(i - focusedIndex);
            if (distanceFromFocus == 0) {
                this.targetSizes[i] = 80;
            } else if (distanceFromFocus == 1) {
                this.targetSizes[i] = 40;
            } else {
                this.targetSizes[i] = 20;
            }
        }

        this.targetXPositions[focusedIndex] = screenCenterX - (this.targetSizes[focusedIndex] / 2);

        for (int i = focusedIndex - 1; i >= 0; i--) {
            this.targetXPositions[i] = this.targetXPositions[i + 1] - gap - this.targetSizes[i];
        }

        for (int i = focusedIndex + 1; i < 5; i++) {
            this.targetXPositions[i] = this.targetXPositions[i - 1] + this.targetSizes[i - 1] + gap;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        for (int i = 0; i < 5; i++) {
            if (this.carouselButtons[i] != null && this.carouselButtons[i].isFocused() && focusedIndex != i) {
                focusedIndex = i;
                this.recalculateCarousel();
            }
        }

        double speedFactor = 0.25;

        for (int i = 0; i < 5; i++) {
            double sizeTarget = this.targetSizes[i];
            double sizeDiff = sizeTarget - this.currentSizes[i];
            this.currentSizes[i] += sizeDiff * speedFactor;

            double xTarget = this.targetXPositions[i];
            double xDiff = xTarget - this.currentXPositions[i];
            this.currentXPositions[i] += xDiff * speedFactor;

            if (Math.abs(sizeDiff) < 0.05) this.currentSizes[i] = sizeTarget;
            if (Math.abs(xDiff) < 0.05) this.currentXPositions[i] = xTarget;

            AbstractWidget btn = this.carouselButtons[i];
            if (btn != null) {
                int exactWidth = (int) Math.round(this.currentSizes[i]);
                btn.setWidth(exactWidth);
                btn.setHeight(exactWidth);

                btn.setX((int) Math.round(this.currentXPositions[i]));
                btn.setY((this.height / 2) - (exactWidth / 2));
            }
        }
    }
}