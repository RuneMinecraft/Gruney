package net.runemc.plugin.scripting;

import net.runemc.plugin.Main;
import org.bukkit.Bukkit;
import org.graalvm.polyglot.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Manages the loading, execution, and unloading of JavaScript scripts.
 */
public class ScriptManager {
    private final Main plugin;
    private final Map<String, Context> scripts;
    private final Map<String, String> scriptContents;
    private final Map<String, Future<?>> runningTasks;
    private final ExecutorService executor;

    public ScriptManager(Main plugin) {
        this.plugin = plugin;
        this.scripts = new ConcurrentHashMap<>();
        this.scriptContents = new ConcurrentHashMap<>();
        this.runningTasks = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Asynchronously loads a script.
     */
    public void loadScriptAsync(String path) {
        executor.submit(() -> {
            try {
                loadScript(path);  // Call the synchronous load method in a separate thread
            } catch (IOException e) {
                plugin.getLogger().severe("Error loading script: " + path + " - " + e.getMessage());
            }
        });
    }

    /**
     * Loads a script into the script manager.
     */
    public void loadScript(String path) throws IOException {
        File scriptFile = new File(plugin.getDataFolder(), path);
        if (!scriptFile.exists()) {
            throw new IOException("Script file not found: " + scriptFile.getAbsolutePath());
        }
        String scriptContent = new String(java.nio.file.Files.readAllBytes(scriptFile.toPath()));
        Context context = Context.newBuilder("js")
                .allowAllAccess(true)
                .build();

        Map<String, Object> bukkitClasses = ReflectionsUtils.wrapClasses(ReflectionsUtils.getAllClasses("org.bukkit"));
        Map<String, Object> paperClasses = ReflectionsUtils.wrapClasses(ReflectionsUtils.getAllClasses("io.papermc"));

        Map<String, Object> allClasses = new HashMap<>(bukkitClasses);
        allClasses.putAll(paperClasses);

        Map<String, Object> pluginUtils = new HashMap<>();
        pluginUtils.put("Plugin", plugin);
        pluginUtils.put("Logger", plugin.getLogger());

        context.getBindings("js").putMember("PluginUtils", pluginUtils);
        context.getBindings("js").putMember("Java", org.graalvm.polyglot.proxy.ProxyObject.fromMap(allClasses));
        context.getBindings("js").putMember("Bukkit", Bukkit.class);
        context.getBindings("js").putMember("Static", new StaticWrapper());

        // Execute the script content
        context.eval("js", scriptContent);

        String scriptName = scriptFile.getName();
        scripts.put(scriptName, context);
        scriptContents.put(scriptName, scriptContent);

        plugin.getLogger().info("Loaded script: " + scriptName);
    }


    /**
     * Unloads a specific script.
     */
    public void unloadScript(String scriptName) {
        Future<?> task = runningTasks.remove(scriptName);
        if (task != null) {
            task.cancel(true);
        }

        if (scripts.remove(scriptName) != null) {
            scriptContents.remove(scriptName);
            plugin.getLogger().info("Unloaded script: " + scriptName);
        } else {
            plugin.getLogger().warning("Script not found: " + scriptName);
        }
    }

    /**
     * Unloads all scripts.
     */
    public void unloadAllScripts() {
        for (Future<?> task : runningTasks.values()) {
            task.cancel(true);
        }
        runningTasks.clear();

        scripts.clear();
        scriptContents.clear();
        plugin.getLogger().info("Unloaded all scripts");
    }

    /**
     * Executes a script asynchronously.
     */
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
                    e.printStackTrace();  // Log the stack trace for more detailed debugging
                }
            } else {
                plugin.getLogger().warning("Script context not found: " + scriptName);
            }
        });

        runningTasks.put(scriptName, task);
    }

    /**
     * Gets the currently loaded scripts.
     */
    public Map<String, Context> getScripts() {
        return scripts;
    }

    /**
     * Shuts down the executor service, gracefully terminating tasks.
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();  // Forcefully shut down if not terminated in time
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        plugin.getLogger().info("ScriptManager shutdown complete");
    }
}
