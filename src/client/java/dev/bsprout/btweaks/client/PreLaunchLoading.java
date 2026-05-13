package dev.bsprout.btweaks.client;

import com.mojang.text2speech.Narrator;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import javax.swing.*;

public class PreLaunchLoading implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        System.setProperty("java.awt.headless", "false");

        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        String runtime = System.getProperty("java.runtime.name").toLowerCase();

        // 1. Check for Android
        if (runtime.contains("android") || os.contains("android")) {
            showErrorAndExit("Android is not supported. BTweaks requires a desktop environment.");
        }

        // 2. Check for Windows ARM
        if (os.contains("win") && (arch.contains("arm") || arch.contains("aarch64"))) {
            showErrorAndExit("Windows ARM is not supported. Please use an x64 (Intel/AMD) version of Windows.");
        }

        // 3. Final validation for supported x64 Desktop (Windows/Linux)
        boolean isWindowsX64 = os.contains("win") && (arch.contains("amd64") || arch.contains("x86_64"));
        boolean isLinuxX64 = os.contains("nux") && (arch.contains("amd64") || arch.contains("x86_64"));

        if (!isWindowsX64 && !isLinuxX64) {
            showErrorAndExit("Unsupported System: " + os + " (" + arch + ")\nBTweaks only supports Windows x64 and Linux x64.");
        }

        LoadingWindow.show();
    }

    private void showErrorAndExit(String specificMessage) {
        String finalMessage = "BTweaks Compatibility Error\n\n" + specificMessage;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        javax.swing.JOptionPane.showMessageDialog(
                null,
                finalMessage,
                "Incompatible Hardware",
                javax.swing.JOptionPane.ERROR_MESSAGE
        );
        System.out.println("[btweaks] Unsupported hardware, halting!");

        System.exit(1);
    }
}