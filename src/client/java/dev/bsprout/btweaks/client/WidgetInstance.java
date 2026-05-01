package dev.bsprout.btweaks.client;

public class WidgetInstance {
    public Widget widget;
    public float x, y; // offset from anchor
    public Anchor anchor;

    public enum Anchor { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    public WidgetInstance(Widget widget, float x, float y, Anchor anchor) {
        this.widget = widget;
        this.x = x;
        this.y = y;
        this.anchor = anchor;
    }

    public float resolvedX(int sw) {
        return switch (anchor) {
            case TOP_RIGHT, BOTTOM_RIGHT -> sw - x;
            default -> x;
        };
    }

    public float resolvedY(int sh) {
        return switch (anchor) {
            case BOTTOM_LEFT, BOTTOM_RIGHT -> sh - y;
            default -> y;
        };
    }
}