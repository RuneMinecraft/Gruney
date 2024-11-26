package net.runemc.plugin.scripting;

import net.runemc.plugin.Main;
import org.bukkit.plugin.java.JavaPlugin;
import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ScriptManager {

    private final Main plugin;
    private final ScriptEngineManager scriptEngineManager;
    private final Map<String, CompiledScript> scripts;
    private final Map<String, Bindings> bindings;

    public ScriptManager(Main plugin) {
        this.plugin = plugin;
        this.scriptEngineManager = new ScriptEngineManager();
        this.scripts = new HashMap<>();
        this.bindings = new HashMap<>();
    }

    public void loadScript(String path) throws IOException, ScriptException {
        File scriptFile = new File(plugin.getDataFolder(), path);
        if (!scriptFile.exists()) {
            throw new IOException("Script file not found: " + scriptFile.getAbsolutePath());
        }

        ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn"); // Change if needed
        if (!(engine instanceof Compilable)) {
            throw new ScriptException("Script engine does not support compilation.");
        }

        Compilable compilable = (Compilable) engine;
        try (FileReader reader = new FileReader(scriptFile)) {
            CompiledScript compiledScript = compilable.compile(reader);
            String scriptName = scriptFile.getName();
            scripts.put(scriptName, compiledScript);
            bindings.put(scriptName, engine.createBindings());
            plugin.getLogger().info("Loaded script: " + scriptName);
        }
    }

    public void unloadScript(String scriptName) {
        if (scripts.remove(scriptName) != null) {
            bindings.remove(scriptName);
            plugin.getLogger().info("Unloaded script: " + scriptName);
        } else {
            plugin.getLogger().warning("Script not found: " + scriptName);
        }
    }

    public void unloadAllScripts() {
        scripts.clear();
        bindings.clear();
        plugin.getLogger().info("Unloaded all scripts");
    }

    public void executeScript(String scriptName) throws ScriptException {
        CompiledScript script = scripts.get(scriptName);
        if (script != null) {
            script.eval();
        }
    }

    public Map<String, CompiledScript> getScripts() {
        return scripts;
    }

    public Map<String, Bindings> getBindings() {
        return bindings;
    }
}
