package net.runemc.plugin.scripting;

import net.runemc.plugin.Main;
import org.bukkit.Bukkit;
import org.graalvm.polyglot.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ScriptManager {
    private final Main plugin;
    private final Map<String, Context> scripts;
    private final Map<String, String> scriptContents;
    private final Map<String, Future<?>> runningTasks;
    private final ExecutorService executor;

    private Map<String, Object> allClasses;
    private Map<String, Object> pluginUtils;

    public ScriptManager(Main plugin) {
        this.plugin = plugin;
        this.scripts = new ConcurrentHashMap<>();
        this.scriptContents = new ConcurrentHashMap<>();
        this.runningTasks = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    public static void loadResources() {
        Map<String, Object> bukkitClasses = ReflectionsUtils.wrapClasses(ReflectionsUtils.getAllClasses("org.bukkit"));
        Map<String, Object> paperClasses = ReflectionsUtils.wrapClasses(ReflectionsUtils.getAllClasses("io.papermc"));

        Map<String, Object> allClasses = new HashMap<>(bukkitClasses);
        allClasses.putAll(paperClasses);

        System.out.println(allClasses);
    }

    public void loadScript(String path) throws IOException {
        File scriptFile = new File(plugin.getDataFolder(), path);
        if (!scriptFile.exists()) {
            throw new IOException("Script file not found: " + scriptFile.getAbsolutePath());
        }
        String scriptContent = new String(java.nio.file.Files.readAllBytes(scriptFile.toPath()));
        Context context = Context.newBuilder("js")
                .option("engine.WarnInterpreterOnly", "false")
                .allowAllAccess(true)
                .build();

        context.getBindings("js").putMember("PluginUtils", pluginUtils);
        context.getBindings("js").putMember("Java", org.graalvm.polyglot.proxy.ProxyObject.fromMap(allClasses));
        context.getBindings("js").putMember("Bukkit", Bukkit.class);
        context.getBindings("js").putMember("Static", new StaticWrapper());

        context.eval("js", scriptContent);

        String scriptName = scriptFile.getName();
        scripts.put(scriptName, context);
        scriptContents.put(scriptName, scriptContent);

        plugin.getLogger().info("Loaded script: " + scriptName);
    }

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

    public Map<String, Context> getScripts() {
        return scripts;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        plugin.getLogger().info("ScriptManager shutdown complete");
    }
}
