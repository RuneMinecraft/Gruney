package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.ScriptManager;
import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import org.bukkit.command.CommandSender;

@Cmd(
        names={"unload-script"}
)
public class UnloadScript extends ICommand {
    ScriptManager scriptManager = Main.get().getScriptManager();
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /unload-script <scriptName>");
            return;
        }

        String scriptName = args[0];
        scriptManager.unloadScript(scriptName);
        sender.sendMessage("Unloaded script: " + scriptName);
    }
}
