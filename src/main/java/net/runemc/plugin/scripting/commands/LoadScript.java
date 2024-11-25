package net.runemc.plugin.scripting.commands;

import net.neoforged.srgutils.IMappingFile;
import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import net.runemc.utils.server.ServerType;
import net.runemc.utils.wrapper.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Cmd(
        names = {"load-script", "loadscript"},
        perms = "rune.admin"
)
public class LoadScript extends ICommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Message.create(sender, "hi").send();
    }
}
