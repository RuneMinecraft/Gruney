package net.runemc.plugin;

import net.runemc.plugin.scripting.RuneClassLoader;
import net.runemc.plugin.scripting.commands.LoadScript;
import net.runemc.plugin.scripting.commands.UnloadScript;
import net.runemc.utils.command.Register;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public final class Main extends JavaPlugin {
    private static RuneClassLoader classLoader;
    public static RuneClassLoader classLoader() {
        return classLoader;
    }

    private static Main instance;
    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {
        try {
            instance = this;

            File pluginDirectory = new File("plugins");
            RuneClassLoader.initialize(pluginDirectory, Main.class.getClassLoader());
            classLoader = RuneClassLoader.getInstance();

            Register reg = Register.get();
            reg.autoRegisterCommands();
        }catch(Exception ignored){}
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
