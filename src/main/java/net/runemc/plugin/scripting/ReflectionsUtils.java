package net.runemc.plugin.scripting;

import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.scanners.Scanner;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionsUtils {
    /**
     * Scans and retrieves all classes within a specified package using Reflections.
     * @param basePackage The base package to scan (e.g., "org.bukkit").
     * @return A map of class simple names to Class objects.
     */
    public static Map<String, Class<?>> getAllClasses(String basePackage) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackage)
                .addScanners(new SubTypesScanner()));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        return classes.stream().collect(Collectors.toMap(Class::getSimpleName, clazz -> clazz));
    }

    /**
     * Wraps a map of Class<?> objects into a map of Objects usable in JavaScript.
     * @param classes A map of class names to Class<?> objects.
     * @return A map of class names to JavaScript-friendly wrappers.
     */
    public static Map<String, Object> wrapClasses(Map<String, Class<?>> classes) {
        return classes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> org.graalvm.polyglot.proxy.ProxyObject.fromMap(
                                Map.of(
                                        "class", entry.getValue(),
                                        "getName", entry.getValue().getName(),
                                        "methods", entry.getValue().getDeclaredMethods()
                                )
                        )
                ));
    }
}
