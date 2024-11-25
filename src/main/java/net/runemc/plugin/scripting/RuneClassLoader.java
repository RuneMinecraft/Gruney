package net.runemc.plugin.scripting;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class RuneClassLoader {
    private final GroovyClassLoader groovyClassLoader;
    private RuneClassLoader(GroovyClassLoader classLoader) {
        this.groovyClassLoader = classLoader;
    }

    public static RuneClassLoader instance(GroovyClassLoader classLoader) {
        return new RuneClassLoader(classLoader);
    }

    public Class<?> load(File script) throws IOException {
        if (!script.exists()) {
            throw new FileNotFoundException("That file does not exist!");
        }
        return groovyClassLoader.parseClass(script);
    }
    public Class<?> load(String source) {
        return groovyClassLoader.parseClass(source);
    }
    public void clearCache() {
        groovyClassLoader.clearCache();
    }
    public Object createInstance(Class<?> loadedClass) throws ReflectiveOperationException {
        return loadedClass.getDeclaredConstructor().newInstance();
    }

    public void close() {
        try {
            groovyClassLoader.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
