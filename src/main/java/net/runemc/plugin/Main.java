package net.runemc.plugin;

import net.runemc.plugin.scripting.*;
import net.runemc.utils.command.Register;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.net.URL;
import java.util.Set;

public final class Main extends JavaPlugin {

    File paperFile = new File(this.getDataFolder()+"/paper.jar");


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
            URL paperUrl = paperFile.toURI().toURL();
            instance = this;
            bindings = new ScriptBindings();

            Context context = Context.newBuilder("js")
                    .option("engine.WarnInterpreterOnly", "false")
                    .allowAllAccess(true)
                    .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL).build())
                    .allowHostClassLookup(className -> true)
                    .build();

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .addUrls(paperUrl)
                    .forPackages("org.bukkit")
                    .addClassLoader(this.getClass().getClassLoader())
                    .addScanners(new SubTypesScanner(false)));


            Set<Class<?>> bukkitClasses = reflections.getSubTypesOf(Object.class);
            System.out.println(bukkitClasses.toString());

            for (Class<?> clazz : bukkitClasses) {
                context.getBindings("js").putMember(clazz.getSimpleName(), clazz);
                System.out.println(clazz.getSimpleName());
            }

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
