package net.runemc.utils;

import net.runemc.utils.wrapper.message.Message;
import org.bukkit.Bukkit;

import java.util.Arrays;

public final class Logger {
    public static void log(String ... messages) {
        Arrays.stream(messages).toList().forEach(message -> Message.create(Bukkit.getConsoleSender(), "[Rune] [LOG]"+ message).send());
    }
    public static void logRaw(String ... messages) {
        Arrays.stream(messages).toList().forEach(message -> Message.create(Bukkit.getConsoleSender(), message).send());
    }
}