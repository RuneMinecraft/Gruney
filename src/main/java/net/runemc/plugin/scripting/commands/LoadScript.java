package net.runemc.plugin.scripting.commands;

import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import net.runemc.utils.wrapper.message.Message;
import org.bukkit.command.CommandSender;

@Cmd(
        names = {"load-script", "loadscript"},
        perms = "rune.admin"
)
public class LoadScript extends ICommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Message.create(sender, "hi").send();
        sender.sendMessage("SI");
    }
}
