package net.runemc.plugin;

import net.runemc.plugin.scripting.ScriptManager;
import net.runemc.utils.command.Register;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private ScriptManager scriptManager;

    private static Main instance;
    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.scriptManager = new ScriptManager(this);

        Register reg = Register.get();
        reg.autoRegisterCommands();
    }

    @Override
    public void onDisable() {
        scriptManager.unloadAllScripts();

    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }
}
