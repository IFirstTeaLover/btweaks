package dev.bsprout.btweaks.client.helpers;

public class Tinter {
    public static int tint(int from, int add, int t) {
        int a = Math.min(255, (from >> 24 & 0xFF) * t);
        int r = Math.min(255, (from >> 16 & 0xFF) + add * t);
        int g = Math.min(255, (from >> 8  & 0xFF) + add * t);
        int b = Math.min(255, (from       & 0xFF) + add * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
