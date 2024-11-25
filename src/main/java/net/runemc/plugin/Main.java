package net.runemc.plugin;

import groovy.lang.GroovyClassLoader;
import net.runemc.plugin.scripting.RuneClassLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Main extends JavaPlugin {
    private final static RuneClassLoader classLoader = RuneClassLoader.instance(new GroovyClassLoader());
    List<String> CONFIG_LOCATIONS = List.of(
            this.getDataFolder().toString()
    );

    @Override
    public void onEnable() {
        try {
            File script = this.findFile().orElse(null);
            Class<?> scriptClass = classLoader.load(script);
            Object clazz = classLoader.createInstance(scriptClass);
            scriptClass.getMethod("run").invoke(clazz);
        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<File> findFile() {
        return CONFIG_LOCATIONS.stream()
                .map(File::new)
                .filter(File::exists)
                .peek(file -> System.out.println(("[Config] Found config file: " + file.getPath())))
                .findFirst();
    }
}
