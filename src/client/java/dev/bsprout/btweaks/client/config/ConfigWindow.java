package dev.bsprout.btweaks.client.config;

import dev.bsprout.btweaks.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import static dev.bsprout.btweaks.client.BtweaksClient.LOGGER;
import static dev.bsprout.btweaks.client.RoundRect.drawImage;
import static dev.bsprout.btweaks.client.RoundRect.drawText;
import static dev.bsprout.btweaks.client.config.ConfigManager.save;
import static dev.bsprout.btweaks.client.config.widgets.CoordinatesConfig.showCoordinatesConfig;
import static dev.bsprout.btweaks.client.config.widgets.FPSConfig.showFpsDisplayConfig;
import static dev.bsprout.btweaks.client.config.widgets.GPUUtilizationConfig.showGPUUtilizationConfig;
import static dev.bsprout.btweaks.client.config.widgets.HitDetectorConfig.showHitDetectorConfig;
import static dev.bsprout.btweaks.client.config.widgets.KeystrokeConfig.showKeystrokeConfig;
import static dev.bsprout.btweaks.client.config.widgets.PingConfig.showPingConfig;
import static dev.bsprout.btweaks.client.config.widgets.RAMUsageConfig.showRAMUsageConfig;

public class ConfigWindow extends Screen {
    public static RoundButton widgetsButton;
    private float animationProgress = 0f;
    private static final float sidebarSize = 0.25f;
    private final List<RoundButton> menuButtons = new ArrayList<>();

    private final List<RoundButton> checkmarks = new ArrayList<>();

    private float scrollAmount = 0;
    private float maxScroll = 0;

    private float targetScroll = 0;
    float configScale = 0;

    private int lastWidth = -1;
    private int lastHeight = -1;

    Identifier check;
    Identifier close;

    private final java.util.Map<RoundButton, CheckmarkData> checkmarkMetadata = new java.util.HashMap<>();

    private record CheckmarkData(String title, String desc, WidgetInstance inst) {}

    public ConfigWindow() {
        super(Component.literal("BTweaks Config"));
    }

    @Override
    protected void init() {
        this.menuButtons.clear();
        this.clearWidgets();
        this.checkmarks.clear();
        this.checkmarkMetadata.clear();

        check = Identifier.fromNamespaceAndPath("btweaks", "textures/gui/check.png");
        close = Identifier.fromNamespaceAndPath("btweaks", "textures/gui/close.png");

        for (WidgetInstance inst : BtweaksClient.renderer.getWidgets()) {
            boolean savedState = ConfigManager.getBoolean(inst.widget.getName(), true);
            inst.widget.setEnabled(savedState);

            RoundButton btn = new RoundButton(0, 0, 0, 0,
                    Component.literal(inst.widget.getName()),
                    0, 0, 0, 0,
                    () -> {
                        this.menuButtons.forEach(this::removeWidget);

                        this.menuButtons.clear();

                        this.showWidgetConfig(inst.widget.getName());
                    },
                    0xFF333333, false
            );
            this.menuButtons.add(btn);
            this.addRenderableWidget(btn);
        }

        widgetsButton = new RoundButton(0, 0, 0, 0, Component.literal("Widgets"), 0, 0, 0, 0, this::init, 0xFF196EE8, false);
        this.addRenderableWidget(widgetsButton);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (this.width != lastWidth || this.height != lastHeight) {
            this.lastWidth = this.width;
            this.lastHeight = this.height;

            this.targetScroll = 0;
            this.scrollAmount = 0;

            updateLayoutVariables();
        }

        animationProgress = updateAnimations(delta);

        configScale = (float) Math.sin((animationProgress * Math.PI) / 2);

        int uiScale = this.minecraft.getWindow().getGuiScale();

        float configWidth = this.width * 0.6f;
        float configHeight = configWidth * 0.62f;

        float currentW = configScaled(configWidth);
        float currentH = configScaled(configHeight);

        float x = (this.width - currentW) / 2f;
        float y = (this.height - currentH) / 2f;

        float margin = configScaled((uiScale * 2));

        float sidebarWidth = currentW * sidebarSize;

        float mainAreaX = x + sidebarWidth;
        float mainAreaW = currentW - sidebarWidth;

        float mainContentX = x + sidebarWidth;

        RoundRect.draw(ctx, x, y, currentW, currentH, 0xF2202020, (int) configScaled((uiScale * 2.5)));

        RoundRect.draw(ctx, x, y, sidebarWidth, currentH, 0xF21A1A1A,
                (int) (configScaled(uiScale * 2.5)), 0, (int) (configScaled(uiScale * 2.5)), 0);

        // prevent scrollable buttons from showing outside config window
        ctx.enableScissor(
                (int) x + 3,
                (int) y + 3,
                (int) (x + currentW - 3),
                (int) (y + currentH - 3)
        );

        updateConfigButtonsLayout(
                x,
                y,
                margin,
                sidebarWidth,
                mainAreaW,
                mainContentX,
                currentH,
                uiScale
        );

        for (RoundButton boxBtn : this.checkmarks) {
            int cardWidth = (int) (mainAreaW - (margin * 2));
            int cardHeight = (int) (cardWidth / 10f);
            int cardX = (int) (mainContentX + margin);

            int cardY = boxBtn.getY() - (cardHeight / 2) + (boxBtn.getHeight() / 2);

            CheckmarkData data = checkmarkMetadata.get(boxBtn);

            drawSettingCard(ctx, cardX, cardY, cardWidth, cardHeight,
                    data.title, data.desc,
                    data.inst.widget.isEnabled(), uiScale);
        }

        super.render(ctx, mouseX, mouseY, delta);

        for (RoundButton boxBtn : this.checkmarks) {
            int checkMargin = uiScale;

            CheckmarkData data = checkmarkMetadata.get(boxBtn);

            boolean enabled = data.inst.widget.isEnabled();

            drawImage(ctx, enabled ? check : close , boxBtn.getX() + checkMargin, boxBtn.getY() + checkMargin, boxBtn.getWidth() - checkMargin * 2, boxBtn.getHeight() - checkMargin * 2);
        }

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
            save();
            this.onClose();
            return true;
        }

        if (keyEvent.isEscape() && this.shouldCloseOnEsc()) {
            save();
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

    private void showWidgetConfig(String configName) {

        WidgetInstance inst = null;
        for (WidgetInstance w : BtweaksClient.renderer.getWidgets()) {
            if (w.widget.getName().equals(configName)) {
                inst = w;
                break;
            }
        }

        if (inst == null) return;

        switch (configName) {
            case "Coordinates":
                showCoordinatesConfig(this, inst);
                break;
            case "Framerate display":
                showFpsDisplayConfig(this, inst);
                break;
            case "Hit detector":
                showHitDetectorConfig(this, inst);
                break;
            case "Keystrokes":
                showKeystrokeConfig(this, inst);
                break;
            case "RAM Usage":
                showRAMUsageConfig(this, inst);
                break;
            case "GPU Utilization display":
                showGPUUtilizationConfig(this, inst);
                break;
            case "Ping display":
                showPingConfig(this, inst);
                break;
        }
    }

    private float updateAnimations(float delta){
        float animationSpeed = 4.0f;
        if (Math.abs(scrollAmount - targetScroll) > 0.1f) {
            scrollAmount += (targetScroll - scrollAmount) * 0.2f;
        } else {
            scrollAmount = targetScroll;
        }
        return Math.min(1.0f, animationProgress + (delta / 20f) * animationSpeed);
    }

    private float configScaled(int var){
        return var * configScale;
    }

    private float configScaled(float var){
        return var * configScale;
    }

    private float configScaled(double var){
        return (float) (var * configScale);
    }

    private void updateConfigButtonsLayout(
            float x,
            float y,
            float margin,
            float sidebarWidth,
            float mainAreaW,
            float mainContentX,
            float currentH,
            int uiScale
    ) {
        widgetsButton.setX((int) (x + margin));
        widgetsButton.setY((int) (y + margin));
        widgetsButton.setWidth((int) (sidebarWidth - margin * 2));
        widgetsButton.setHeight(widgetsButton.getWidth() / 5);

        int buttonRounding = (int) configScaled(uiScale);

        widgetsButton.setRadius(
                buttonRounding,
                buttonRounding,
                buttonRounding,
                buttonRounding
        );

        int buttonWidth = (int) (mainAreaW - (margin * 2));

        int buttonHeight = (int) (buttonWidth / 10f);

        int i = 0;

        for (RoundButton btn : this.menuButtons) {
            int rounding = (int) configScaled(uiScale * 1.6f);

            btn.setWidth(buttonWidth);
            btn.setHeight(buttonHeight);

            btn.setX((int) (mainContentX + margin));

            btn.setY((int) (
                    y + margin +
                            (i * (buttonHeight + margin)) -
                            scrollAmount
            ));

            btn.setRadius(rounding, rounding, rounding, rounding);

            i++;
        }
        int cardWidth = 0;
        int j = 0;
        cardWidth = (int) (mainAreaW - (margin * 2));
        int cardHeight = (int) (cardWidth / 10f);

        for (RoundButton boxBtn : this.checkmarks) {
            int cardX = (int) (mainContentX + margin);
            int cardY = (int) (y + margin + (j * (cardHeight + margin)) - scrollAmount);

            int boxSize = (int) (cardHeight * 0.75f);
            int padding = uiScale * 3;

            int verticalPadding = (cardHeight - boxSize) / 2;
            int horizontalPadding = verticalPadding;

            boxBtn.setWidth(boxSize);
            boxBtn.setHeight(boxSize);
            boxBtn.setX(cardX + cardWidth - boxSize - horizontalPadding);
            boxBtn.setY(cardY + verticalPadding);
            int boxRadius = (int)configScaled(uiScale * 1.2f);
            boxBtn.setRadius(boxRadius, boxRadius, boxRadius, boxRadius);
            j++;
        }

        float checkmarkHeight = (j * (cardHeight + margin));
        float menuHeight = (i * (buttonHeight + margin));
        this.maxScroll = Math.max(0, Math.max(menuHeight, checkmarkHeight) - currentH + (margin * 2));
    }

    public void newCheckmark(WidgetInstance inst, String title, String desc) {
        boolean isEnabled = inst.widget.isEnabled();
        final RoundButton[] checkBoxArr = new RoundButton[1];
        inst.widget.setEnabled(isEnabled);
        checkBoxArr[0] = new RoundButton(0, 0, 0, 0,
                Component.literal(""),
                0, 0, 0, 0,
                () -> {
                    boolean newState = !inst.widget.isEnabled();
                    inst.widget.setEnabled(newState);

                    if (checkBoxArr[0] != null) {
                        checkBoxArr[0].setColor(newState ? 0xFF4CAF50 : 0xFF333333);
                    }

                    ConfigManager.set(inst.widget.getName(), newState);
                },
                inst.widget.isEnabled() ? 0xFF4CAF50 : 0xFF333333, true
        );

        checkmarkMetadata.put(checkBoxArr[0], new CheckmarkData(title, desc, inst));

        this.checkmarks.add(checkBoxArr[0]);
        this.addRenderableWidget(checkBoxArr[0]);
    }

    public static void drawSettingCard(GuiGraphics graphics, int x, int y, int width, int height, String title, String desc, boolean enabled, int uiScale) {
        int padding = uiScale * 3;
        int gap = uiScale * 2;

        RoundRect.draw(graphics, x, y, width, height, 0x901A1A1A, (int) (uiScale * 1.2));

        drawText(graphics, title, x + padding, y + padding, 0, 0, 0xFFFFFFFF, "gsans", "left");

        int descY = y + height - padding;
        drawText(graphics, desc, x + padding, descY, 0, 0, 0xFFAAAAAA, "gsans", "left");
    }

    private void updateLayoutVariables() {
        int uiScale = mc.getWindow().getGuiScale();

        float configWidth = this.width * 0.6f;
        float configHeight = configWidth * 0.62f;
        float currentW = configScaled(configWidth);
        float currentH = configScaled(configHeight);

        float x = (this.width - currentW) / 2f;
        float y = (this.height - currentH) / 2f;
        float margin = configScaled((uiScale * 2));
        float sidebarWidth = currentW * sidebarSize;
        float mainAreaW = currentW - sidebarWidth;
        float mainContentX = x + sidebarWidth;

        widgetsButton.setX((int) (x + margin));
        widgetsButton.setY((int) (y + margin));
        widgetsButton.setWidth((int) (sidebarWidth - margin * 2));
        widgetsButton.setHeight(widgetsButton.getWidth() / 5);

        int buttonRounding = (int) configScaled(uiScale);
        widgetsButton.setRadius(buttonRounding, buttonRounding, buttonRounding, buttonRounding);

        int buttonWidth = (int) (mainAreaW - (margin * 2));
        int buttonHeight = (int) (buttonWidth / 10f);

        for (int i = 0; i < this.menuButtons.size(); i++) {
            RoundButton btn = this.menuButtons.get(i);
            btn.setWidth(buttonWidth);
            btn.setHeight(buttonHeight);
            btn.setX((int) (mainContentX + margin));
            btn.setY((int) (y + margin + (i * (buttonHeight + margin)) - scrollAmount));

            int r = (int) configScaled(uiScale * 1.6f);
            btn.setRadius(r, r, r, r);
        }

        for (int j = 0; j < this.checkmarks.size(); j++) {
            RoundButton boxBtn = this.checkmarks.get(j);
            int cardY = (int) (y + margin + (j * (buttonHeight + margin)) - scrollAmount);
            int boxSize = (int) (buttonHeight * 0.75f);
            int horizontalPadding = (buttonHeight - boxSize) / 2;

            boxBtn.setWidth(boxSize);
            boxBtn.setHeight(boxSize);
            boxBtn.setX((int)(mainContentX + mainAreaW - margin - boxSize - horizontalPadding));
            boxBtn.setY(cardY + horizontalPadding);

            int boxRadius = (int)configScaled(uiScale * 1.2f);
            boxBtn.setRadius(boxRadius, boxRadius, boxRadius, boxRadius);
        }

        float totalContentHeight = Math.max(this.menuButtons.size(), this.checkmarks.size()) * (buttonHeight + margin);
        this.maxScroll = Math.max(0, totalContentHeight - currentH + (margin * 2));
    }

    public static void initializeWidgetsFromConfig() {
        if (BtweaksClient.renderer != null) {
            LOGGER.info("[btweaks] World load detected. Syncing widget states with config.");

            for (WidgetInstance inst : BtweaksClient.renderer.getWidgets()) {
                boolean savedState = ConfigManager.getBoolean(inst.widget.getName(), true);

                inst.widget.setEnabled(savedState);
            }
        }
    }
}