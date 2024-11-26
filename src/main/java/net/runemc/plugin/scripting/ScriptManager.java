package net.runemc.plugin.scripting;

import net.runemc.plugin.Main;
import org.graalvm.polyglot.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScriptManager {

    private final Main plugin;
    private final Map<String, Context> scripts;
    private final Map<String, Value> bindings;
    private final Map<String, String> scriptContents;

    public ScriptManager(Main plugin) {
        this.plugin = plugin;
        this.scripts = new HashMap<>();
        this.bindings = new HashMap<>();
        this.scriptContents = new HashMap<>();
    }

    public void loadScript(String path) throws IOException {
        File scriptFile = new File(plugin.getDataFolder(), path);
        if (!scriptFile.exists()) {
            throw new IOException("Script file not found: " + scriptFile.getAbsolutePath());
        }

        String scriptContent = new String(java.nio.file.Files.readAllBytes(scriptFile.toPath()));

        Context context = Context.create();
        Value result = context.eval("js", scriptContent);

        String scriptName = scriptFile.getName();
        scripts.put(scriptName, context);
        bindings.put(scriptName, result);
        scriptContents.put(scriptName, scriptContent); // Save the script content
        plugin.getLogger().info("Loaded script: " + scriptName);
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

    public void executeScript(String scriptName) {
        Context context = scripts.get(scriptName);
        String scriptContent = scriptContents.get(scriptName);
        if (context != null && scriptContent != null) {
            context.eval("js", scriptContent);
            plugin.getLogger().info("Executed script: " + scriptName);
        } else {
            plugin.getLogger().warning("Script not found: " + scriptName);
        }
    }

    public Map<String, Context> getScripts() {
        return scripts;
    }

    public Map<String, Value> getBindings() {
        return bindings;
    }
}
