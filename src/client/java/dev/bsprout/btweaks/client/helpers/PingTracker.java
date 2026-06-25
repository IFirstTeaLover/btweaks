package dev.bsprout.btweaks.client.helpers;

public class PingTracker {
    private static int livePing = 0;

    public static void setLivePing(int ping) {
        livePing = ping;
    }

    public static int getLivePing() {
        return livePing;
    }
}