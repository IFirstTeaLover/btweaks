package dev.bsprout.btweaks.client.config;

import dev.bsprout.brapi.client.BFont;
import dev.bsprout.brapi.client.BRender;
import dev.bsprout.brapi.client.BTexture;
import dev.bsprout.brapi.client.NineSlice;
import dev.bsprout.btweaks.client.*;
import dev.bsprout.btweaks.client.buttons.BigConfigButton;
import dev.bsprout.btweaks.client.buttons.ConfigButton;
import dev.bsprout.btweaks.client.buttons.RoundButton;
import dev.bsprout.btweaks.client.helpers.WidgetGeneral;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

import static dev.bsprout.brapi.client.BUtils.nineslicify;
import static dev.bsprout.btweaks.client.BtweaksClient.mc;
import static dev.bsprout.btweaks.client.BtweaksClient.LOGGER;
import static dev.bsprout.btweaks.client.RoundRect.*;
import static dev.bsprout.btweaks.client.config.ConfigManager.save;
import static dev.bsprout.btweaks.client.config.widgets.CoordinatesConfig.showCoordinatesConfig;
import static dev.bsprout.btweaks.client.config.widgets.FPSConfig.showFpsDisplayConfig;
import static dev.bsprout.btweaks.client.config.widgets.GPUUtilizationConfig.showGPUUtilizationConfig;
import static dev.bsprout.btweaks.client.config.widgets.HitDetectorConfig.showHitDetectorConfig;
import static dev.bsprout.btweaks.client.config.widgets.KeystrokeConfig.showKeystrokeConfig;
import static dev.bsprout.btweaks.client.config.widgets.PingConfig.showPingConfig;
import static dev.bsprout.btweaks.client.config.widgets.RAMUsageConfig.showRAMUsageConfig;

public class ConfigWindow extends Screen {
    public static BigConfigButton widgetsButton;
    private float animationProgress = 0.6f;
    private static final float sidebarSize = 0.25f;
    private final List<RoundButton> menuButtons = new ArrayList<>();

    private final List<RoundButton> checkmarks = new ArrayList<>();
    private boolean closing = false;
    private float scrollAmount = 0;
    private float maxScroll = 0;

    private float targetScroll = 0;
    static float configScale = 0;

    private int lastWidth = -1;
    private int lastHeight = -1;

    public static double globalDelta = 1.0;
    private float transitionAlpha = 0f;
    private boolean fadingIn = false;
    private Runnable postFadeAction = null;
    private float FADE_SPEED = 0.01f;

    private NineSlice wrapper = null;
    private NineSlice header = null;
    private BTexture close = null;
    private BTexture back = null;
    private BTexture forward = null;
    private NineSlice leftPanel = null;

    private BFont notoFont = null;
    private BFont mcFont = null;

    private final java.util.Map<RoundButton, CheckmarkData> checkmarkMetadata = new java.util.HashMap<>();

    private record CheckmarkData(String title, String desc, WidgetInstance inst) {}

    private BRender bRender = new BRender();

    public ConfigWindow() {
        super(Component.literal("BTweaks Config"));
    }

    private AbstractWidget closeButton = null;
    private AbstractWidget backButton = null;
    private AbstractWidget forwardButton = null;

    @Override
    protected void init() {
        BTexture cardTexture = new BTexture(Identifier.fromNamespaceAndPath("btweaks", "textures/gui/background.png"));
        wrapper = nineslicify(cardTexture, 4, 4, 4, 4);

        BTexture headerTexture = new BTexture(Identifier.fromNamespaceAndPath("btweaks", "textures/gui/mc_header.png"));
        header = nineslicify(headerTexture, 4, 4, 4, 4);

        BTexture leftPanelTexture = new BTexture(Identifier.fromNamespaceAndPath("btweaks", "textures/gui/left_panel.png"));
        leftPanel = nineslicify(leftPanelTexture, 4, 4, 4, 4);

        close = new BTexture(Identifier.fromNamespaceAndPath("btweaks", "textures/gui/close.png"));
        back = new BTexture(Identifier.fromNamespaceAndPath("btweaks", "textures/gui/back.png"));
        forward = new BTexture(Identifier.fromNamespaceAndPath("btweaks", "textures/gui/forward.png"));

        closeButton = new ConfigButton(0, 0, 20, 20, () -> {
            closing = true;
        }, close);

        backButton = new ConfigButton(0, 0, 20, 20, () -> {

        }, back);

        forwardButton = new ConfigButton(0, 0, 20, 20, () -> {

        }, forward);

        notoFont = new BFont(Identifier.fromNamespaceAndPath("btweaks", "font/notosans.ttf"));

        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(forwardButton);

        this.menuButtons.clear();
        this.checkmarks.clear();
        this.checkmarkMetadata.clear();

//        for (WidgetInstance inst : BtweaksClient.renderer.getWidgets()) {
//            boolean savedState = ConfigManager.getBoolean(inst.widget.getName(), true);
//            inst.widget.setEnabled(savedState);
//
//            RoundButton btn = new RoundButton(0, 0, 0, 0,
//                    Component.literal(inst.widget.getName()),
//                    0, 0, 0, 0,
//                    () -> {
//                        this.fadingIn = true;
//                        this.postFadeAction = () -> {
//                            this.menuButtons.clear();
//                            this.showWidgetConfig(inst.widget.getName());
//                        };
//                    },
//                    0xFF333333, false
//            );
//            this.menuButtons.add(btn);
//            this.addRenderableWidget(btn);
//        }
//
        widgetsButton = new BigConfigButton(0, 0, 0, 0,
                Component.literal("Widgets"), () -> {
                    this.fadingIn = true;
                    this.postFadeAction = this::init;
                }, wrapper
        );
        this.addRenderableWidget(widgetsButton);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (this.width != lastWidth || this.height != lastHeight) {
            this.lastWidth = this.width;
            this.lastHeight = this.height;

            this.targetScroll = 0;
            this.scrollAmount = 0;

            //updateLayoutVariables();
        }

        animationProgress = updateAnimations(delta);

        configScale = (float) Math.sin((animationProgress * Math.PI) / 2);
        FADE_SPEED = 0.5f * delta;
        int uiScale = this.minecraft.getWindow().getGuiScale();

        float screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        float screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

        float configWidth = (float) (screenWidth / 1.6);
        float configHeight = (float) (configWidth / 1.6);

        float currentW = configScaled(configWidth);
        float currentH = configScaled(configHeight);

        float x = (this.width - currentW) / 2f;
        float centeredY = (this.height - currentH) / 2f;
        float startY = closing ? centeredY : this.height;
        float y = startY + (centeredY - startY) * configScale;

        float margin = configScaled((uiScale * 2));

        float sidebarWidth = currentW * sidebarSize;

        float mainAreaX = x + sidebarWidth;
        float mainAreaW = currentW - sidebarWidth;

        float mainContentX = x + sidebarWidth;

        float closePad = uiScale;
        float closeSize = (currentH / 12f) - 6;

        float headerH = (currentH / 12f);
        float headerInnerY = y + 2;
        float headerInnerH = headerH - 5;
        float closeY = headerInnerY + (headerInnerH - closeSize) / 2f;
        float closeX = x + currentW - closeSize - 2;

        bRender.drawTexture9Slice(wrapper, x, y, currentW, currentH, 0xFFFFFFFF, false, 1);

        bRender.drawTexture9Slice(header, x, y, currentW, headerH, 0xFFFFFFFF, false, 2);

        bRender.drawTexture9Slice(leftPanel, x, y + headerH, sidebarWidth, currentH - headerH, 0xFFFFFFFF, false, 2);

        float headerTextX = x + 4;
        float headerTextY = y + 13 * configScale;
        bRender.drawText(notoFont, "BTweaks", headerTextX, headerTextY, 15 * configScale, 0xFFFFFFFF, 3);

        closeButton.setPosition((int)closeX, (int)closeY + 1);
        closeButton.setSize((int) closeSize, (int) closeSize);

        forwardButton.setPosition((int) (closeX - closeSize - 1), (int) closeY + 1);
        forwardButton.setSize((int)closeSize, (int) closeSize);

        backButton.setPosition((int) (closeX - closeSize * 2 - 2), (int) closeY + 1);
        backButton.setSize((int)closeSize, (int) closeSize);

        widgetsButton.setPosition((int) x + uiScale, (int) (y + headerH + uiScale));
        widgetsButton.setSize((int)sidebarWidth - uiScale * 2, (int)(sidebarWidth - uiScale * 2) / 5);

        super.render(ctx, mouseX, mouseY, delta);
        bRender.flush(ctx);
        if (true) return;
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

        if (transitionAlpha > 0) {
            int alphaInt = (int) (transitionAlpha * 255);
            int color = (alphaInt << 24) | (0x00202020);
            RoundRect.draw(ctx, mainContentX, y, mainAreaW, currentH, scaleAlpha(color), (int) configScaled((uiScale * 2.5)));
        }

        for (RoundButton boxBtn : this.checkmarks) {
            int checkMargin = uiScale;

            CheckmarkData data = checkmarkMetadata.get(boxBtn);

            boolean enabled = data.inst.widget.isEnabled();

            //drawImage(ctx, enabled ? check : close , boxBtn.getX() + checkMargin, boxBtn.getY() + checkMargin, boxBtn.getWidth() - checkMargin * 2, boxBtn.getHeight() - checkMargin * 2);
        }
        bRender.flush(ctx);
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
            closing = true;
            return true;
        }

        if (keyEvent.isEscape() && this.shouldCloseOnEsc()) {
            save();
            closing = true;
            return true;
        }

        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBlurredBackground(guiGraphics);
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

    private float updateAnimations(float delta) {
        float animationSpeed = 2.0f;
        if (Math.abs(scrollAmount - targetScroll) > 0.1f) {
            scrollAmount += (targetScroll - scrollAmount) * 0.2f;
        } else {
            scrollAmount = targetScroll;
        }

        if (closing) {
            float newProgress = animationProgress - (delta / 20f) * animationSpeed * 4;
            if (newProgress <= 0.0f) {
                this.minecraft.setScreen(null);
                return 0.0f;
            }
            return newProgress;
        }

        return Math.min(1.0f, animationProgress + (delta / 20f) * animationSpeed);
    }

    private static int scaleAlpha(int color) {
        float t = (configScale - 0.6f) / 0.4f;
        float clamped = Math.max(0f, Math.min(1f, t));
        int a = (int) ((color >> 24 & 0xFF) * clamped);
        return (a << 24) | (color & 0x00FFFFFF);
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
            WidgetGeneral.setGlobalWidgetColor(0xA6292929);
            for (WidgetInstance inst : BtweaksClient.renderer.getWidgets()) {
                boolean savedState = ConfigManager.getBoolean(inst.widget.getName(), true);

                inst.widget.setEnabled(savedState);
            }
        }
    }

    public static float scaleSize(float baseSize, float animProgress, float refDimension, float screenDimension) {
        float resolutionScale = screenDimension / refDimension;
        return baseSize * resolutionScale * animProgress;
    }

    @Override
    public void onClose() {
        closing = true;
    }
}