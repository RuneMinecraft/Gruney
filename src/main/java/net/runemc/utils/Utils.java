package net.runemc.utils;

import net.runemc.plugin.scripting.ReflectionsUtils;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public interface Utils {
    default String Colour(String message) {
        return translateAlternateColorCodes('&', message);
    }

    static Map<String, Object> getClasses() {
        Map<String, Object> bukkitClasses = ReflectionsUtils.wrapClasses(ReflectionsUtils.getAllClasses("org.bukkit"));
        Map<String, Object> paperClasses = ReflectionsUtils.wrapClasses(ReflectionsUtils.getAllClasses("io.papermc"));

        Map<String, Object> allClasses = new HashMap<>(bukkitClasses);
        allClasses.putAll(paperClasses);
        return allClasses;
    }
}
