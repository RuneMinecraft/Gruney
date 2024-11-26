package net.runemc.plugin.scripting;

import net.runemc.plugin.Main;
import org.graalvm.polyglot.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ScriptManager {

    private final Main plugin;
    private final Map<String, Context> scripts;
    private final Map<String, Value> bindings;
    private final Map<String, String> scriptContents;
    private final Map<String, Future<?>> runningTasks;
    private final ExecutorService executor;

    public ScriptManager(Main plugin) {
        this.plugin = plugin;
        this.scripts = new HashMap<>();
        this.bindings = new HashMap<>();
        this.scriptContents = new HashMap<>();
        this.runningTasks = new HashMap<>();
        this.executor = Executors.newCachedThreadPool();
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
        scriptContents.put(scriptName, scriptContent);
        plugin.getLogger().info("Loaded script: " + scriptName);
    }

    public void unloadScript(String scriptName) {
        Future<?> task = runningTasks.remove(scriptName);
        if (task != null) {
            task.cancel(true);
        }

        if (scripts.remove(scriptName) != null) {
            bindings.remove(scriptName);
            scriptContents.remove(scriptName);
            plugin.getLogger().info("Unloaded script: " + scriptName);
        } else {
            plugin.getLogger().warning("Script not found: " + scriptName);
        }
    }

    public void unloadAllScripts() {
        for (Future<?> task : runningTasks.values()) {
            task.cancel(true);
        }
        runningTasks.clear();

        scripts.clear();
        bindings.clear();
        scriptContents.clear();
        plugin.getLogger().info("Unloaded all scripts");
    }

    public void executeScript(String scriptName) {
        String scriptContent = scriptContents.get(scriptName);

        if (scriptContent == null) {
            plugin.getLogger().warning("Script not found: " + scriptName);
            return;
        }

        Future<?> task = executor.submit(() -> {
            Context context = scripts.get(scriptName);
            if (context != null) {
                try {
                    context.eval("js", scriptContent);
                    plugin.getLogger().info("Executed script: " + scriptName);
                } catch (Exception e) {
                    plugin.getLogger().severe("Error executing script: " + scriptName + " - " + e.getMessage());
                }
            } else {
                plugin.getLogger().warning("Script context not found: " + scriptName);
            }
        });

        runningTasks.put(scriptName, task);
    }

    public Map<String, Context> getScripts() {
        return scripts;
    }

    public Map<String, Value> getBindings() {
        return bindings;
    }

    public void shutdown() {
        executor.shutdownNow();
        plugin.getLogger().info("ScriptManager shutdown complete");
    }
}
