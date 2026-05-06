package dev.bsprout.btweaks.client.config;

import dev.bsprout.btweaks.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static dev.bsprout.btweaks.client.BtweaksClient.LOGGER;
import static dev.bsprout.btweaks.client.config.widgets.CoordinatesConfig.showCoordinatesConfig;
import static dev.bsprout.btweaks.client.config.widgets.FPSConfig.showFpsDisplayConfig;
import static dev.bsprout.btweaks.client.config.widgets.HitDetectorConfig.showHitDetectorConfig;
import static dev.bsprout.btweaks.client.config.widgets.KeystrokeConfig.showKeystrokeConfig;
import static dev.bsprout.btweaks.client.config.widgets.RAMUsageConfig.showRAMUsageConfig;

public class ConfigWindow extends Screen {
    public static RoundButton widgetsButton;
    private float animationProgress = 0f;
    private static final float sidebarSize = 0.25f;
    private final List<RoundButton> menuButtons = new ArrayList<>();
    private float scrollAmount = 0;
    private float maxScroll = 0;
    private float targetScroll = 0;

    public ConfigWindow() {
        super(Component.literal("BTweaks Config"));
    }

    @Override
    protected void init() {
        this.menuButtons.clear();
        this.clearWidgets();

        for (WidgetInstance inst : BtweaksClient.renderer.getWidgets()) {
            RoundButton btn = new RoundButton(0, 0, 0, 0,
                    Component.literal(inst.widget.getName()),
                    0, 0, 0, 0,
                    () -> {
                        this.menuButtons.clear();

                        this.clearWidgets();

                        inst.widget.setEnabled(!inst.widget.isEnabled());
                        this.showWidgetConfig(inst.widget.getName());
                    },
                    0xFF333333
            );
            this.menuButtons.add(btn);
            this.addRenderableWidget(btn);
        }

        widgetsButton = new RoundButton(0, 0, 0, 0, Component.literal("Widgets"), 0, 0, 0, 0, () -> {
            LOGGER.info("Widgets tab clicked!");
        }, 0xFF196EE8);
        this.addRenderableWidget(widgetsButton);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        float animationSpeed = 4.0f;
        if (Math.abs(scrollAmount - targetScroll) > 0.1f) {
            scrollAmount += (targetScroll - scrollAmount) * 0.2f;
        } else {
            scrollAmount = targetScroll;
        }
        animationProgress = Math.min(1.0f, animationProgress + (delta / 20f) * animationSpeed);

        float configScale = (float) Math.sin((animationProgress * Math.PI) / 2);

        int uiScale = this.minecraft.getWindow().getGuiScale();

        float configWidth = this.width * 0.6f;
        float configHeight = configWidth * 0.62f;

        float currentW = configWidth * configScale;
        float currentH = configHeight * configScale;

        float x = (this.width - currentW) / 2f;
        float y = (this.height - currentH) / 2f;

        float margin = (uiScale * 2) * configScale;

        float sidebarWidth = currentW * sidebarSize;

        float mainAreaX = x + sidebarWidth;
        float mainAreaW = currentW - sidebarWidth;

        float mainContentX = x + sidebarWidth;

        ctx.enableScissor(
                (int) x,
                (int) y,
                (int) (x + currentW),
                (int) (y +currentH - 1)
        );


        widgetsButton.setX((int) (x + margin));
        widgetsButton.setY((int) (y + margin));
        widgetsButton.setWidth((int) (sidebarWidth - margin * 2));
        widgetsButton.setHeight(widgetsButton.getWidth() / 5);
        widgetsButton.setRadius((int) ((uiScale * 2.5) * configScale), (int) ((uiScale * 1.5) * configScale), (int) ((uiScale * 1.5) * configScale), (int) ((uiScale * 1.5) * configScale));

        int buttonWidth = (int) (mainAreaW - (margin * 2));
        int buttonHeight = (int) (buttonWidth / 10f);
        int i = 0;
        for (var widget : this.menuButtons) {
            if (widget instanceof RoundButton btn) {
                int rounding = (int)(uiScale * 2 * configScale);
                btn.setWidth((buttonWidth));
                btn.setHeight(buttonHeight);
                btn.setX((int)(mainContentX + margin));
                btn.setY((int)(y + margin + (i * (buttonHeight + margin)) - scrollAmount));
                btn.setRadius(rounding, rounding, rounding, rounding);
                i++;
            }
        }

        RoundRect.draw(ctx, x, y, currentW, currentH, 0xF2202020, (int) (uiScale * 4 * configScale));

        RoundRect.draw(ctx, x, y, sidebarWidth, currentH, 0xF21A1A1A,
                (int) (uiScale * 4 * configScale), 0, (int) (uiScale * 4 * configScale), 0);

        this.maxScroll = Math.max(0, (i * buttonHeight) - currentH + (margin * 2));

        super.render(ctx, mouseX, mouseY, delta);

        ctx.disableScissor();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.targetScroll = Math.max(0, Math.min(maxScroll, (float) (targetScroll - (verticalAmount * 30))));
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        // key code 344 is right shift
        if (keyEvent.key() == 344) {
            this.onClose();
            return true;
        }


        if (keyEvent.isEscape() && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }

        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (animationProgress > 0.05f) {
            guiGraphics.blurBeforeThisStratum();
        }
    }

    private void showWidgetConfig(String config){
        switch (config) {
            case "Coordinates":
                showCoordinatesConfig();
            case "Framerate display":
                showFpsDisplayConfig();
            case "Hit detector":
                showHitDetectorConfig();
            case "Keystroke":
                showKeystrokeConfig();
            case "RAM Usage":
                showRAMUsageConfig();
        }
    }
}