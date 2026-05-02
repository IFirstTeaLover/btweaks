package dev.bsprout.btweaks.client;

public class WidgetInstance {
    public Widget widget;
    public int col, row;
    public Anchor anchor;

    public enum Anchor { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER }

    public WidgetInstance(Widget widget, int col, int row, Anchor anchor) {
        this.widget = widget;
        this.col = col;
        this.row = row;
        this.anchor = anchor;
    }
}