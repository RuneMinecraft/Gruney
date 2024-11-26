package net.runemc.plugin;


import net.runemc.plugin.scripting.ScriptManager;
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
    private ScriptManager scriptManager;

    private static Main instance;

    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Register reg = Register.get();
        reg.autoRegisterCommands();
        scriptManager = new ScriptManager(this);


    }


    @Override
    public void onDisable() {
        scriptManager.unloadAllScripts();

    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

}
