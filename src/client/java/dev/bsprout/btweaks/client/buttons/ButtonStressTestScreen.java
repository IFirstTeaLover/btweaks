package dev.bsprout.btweaks.client.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ButtonStressTestScreen extends Screen {
    private static final String TEST_LABEL = "ABCDEFGHIJKLMNOPQRST"; // 20 chars

    public ButtonStressTestScreen() {
        super(Component.literal("Stress Test"));
    }

    @Override
    protected void init() {
        int btnW = 160, btnH = 20, pad = 4;
        int cols = this.width / (btnW + pad);
        for (int i = 0; i < 100; i++) {
            int col = i % cols;
            int row = i / cols;
            this.addRenderableWidget(Button.builder(
                    Component.literal(TEST_LABEL),
                    btn -> {}
            ).bounds(col * (btnW + pad) + pad, row * (btnH + pad) + pad, btnW, btnH).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
    }
}