package dev.bsprout.btweaks.client;

import com.mojang.text2speech.Narrator;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import javax.swing.*;

public class PreLaunchLoading implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        LoadingWindow.show();
    }
}