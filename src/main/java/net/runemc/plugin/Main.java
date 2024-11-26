package net.runemc.plugin;

import net.runemc.plugin.scripting.*;
import net.runemc.utils.Utils;
import net.runemc.utils.command.Register;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

public final class Main extends JavaPlugin {
    private ScriptBindings bindings;
    public ScriptBindings bindings() {
        return this.bindings;
    }

    private static Main instance;
    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {
        try {
            instance = this;
            bindings = new ScriptBindings();

            Context context = Context.newBuilder("js")
                    .option("engine.WarnInterpreterOnly", "false")
                    .allowAllAccess(true)
                    .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL).build())
                    .allowHostClassLookup(className -> true)
                    .build();

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages("org.bukkit")
                    .addScanners(new SubTypesScanner(true)));

            Map<String, Object> bukkitClasses = ReflectionsUtils.wrapClasses(ReflectionsUtils.getAllClasses("org.bukkit"));
            ScriptManager.loadResources();

            context.getBindings("js").putMember("Bukkit", Bukkit.class);
            context.getBindings("js").putMember("Static", new StaticWrapper());
            context.enter();

            this.bindings().setSharedBindings(context.getBindings("js"));
            Script script = new Script("scripts/bootstrap.js", bindings().sharedBindings());
            script.load();
            script.execute();
            script.unload();

            Register.get().autoRegisterCommands();
            Register.get().autoRegisterListeners();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        bindings.getAllScripts().forEach((s, scr) -> {
            System.out.println("Unloading script "+s+"!");
            int code = scr.unload();
            System.out.println("Finished with code "+code);
        });
    }
}
