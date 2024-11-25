package net.runemc.plugin;

import net.runemc.plugin.scripting.RuneClassLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public final class Main extends JavaPlugin {
    private RuneClassLoader classLoader;

    @Override
    public void onEnable() {
        try {
            File pluginClassesDir = new File(getDataFolder(), "classes");
            if (!pluginClassesDir.exists() && !pluginClassesDir.mkdirs()) {
                getLogger().severe("Failed to create plugin classes directory!");
                return;
            }

            classLoader = RuneClassLoader.fromPluginDirectory(pluginClassesDir, getClass().getClassLoader());
            getLogger().info("RuneClassLoader initialized.");

            classLoader.compileJavaFiles(pluginClassesDir);
            getLogger().info("All .java files compiled successfully.");

            String className = "net.runemc.Test";
            Class<?> myClass = classLoader.loadClass(className);
            Object instance = classLoader.createInstance(className);

            getLogger().info("Loaded class: " + myClass.getName());
            getLogger().info("Instance created: " + instance);

        } catch (IOException | ReflectiveOperationException e) {
            getLogger().severe("Failed to load classes: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        if (classLoader != null) {
            classLoader.unload();
            getLogger().info("RuneClassLoader unloaded.");
        }
    }

    public Optional<File> findConfigFile(String fileName) {
        File configFile = new File(getDataFolder(), fileName);
        if (configFile.exists()) {
            getLogger().info("[Config] Found config file: " + configFile.getPath());
            return Optional.of(configFile);
        }
        getLogger().warning("[Config] Config file not found: " + fileName);
        return Optional.empty();
    }
}
