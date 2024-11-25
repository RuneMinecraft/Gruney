package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.scripting.RuneClassLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UnloadScript implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(strings.length > 0)){
            return false;
        }
        String name = strings[0];

        //unload here

        return false;
    }
}
