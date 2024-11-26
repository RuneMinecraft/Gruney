package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.RuneClassLoader;
import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import net.runemc.utils.wrapper.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Cmd(
        names={"unload-script", "unloadscript"},
        perms="rune.admin"
)
public class UnloadScript extends ICommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length != 1) {
                //TODO: ERROR MESSAGE
                return;
            }

            Message.create(sender, "bye").send(true);

            RuneClassLoader classLoader = Main.classLoader();
            classLoader.unloadScript(args[0]);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
