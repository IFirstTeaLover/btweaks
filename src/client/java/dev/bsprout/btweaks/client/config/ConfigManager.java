package dev.bsprout.btweaks.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("btweaks.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static JsonObject configCache = new JsonObject();

    public static void load() {
        File file = PATH.toFile();
        if (!file.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            configCache = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            configCache = new JsonObject();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(PATH.toFile())) {
            GSON.toJson(configCache, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        if (configCache.has(key)) {
            return configCache.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    public static String getString(String key, String defaultValue) {
        if (configCache.has(key)) {
            return configCache.get(key).getAsString();
        }
        return defaultValue;
    }


    public static void set(String key, boolean value) {
        configCache.addProperty(key, value);
        save(); // Save immediately when a setting changes
    }

    public static void set(String key, String value) {
        configCache.addProperty(key, value);
        save();
    }
}