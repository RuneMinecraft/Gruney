package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.ScriptManager;
import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import org.bukkit.command.CommandSender;

@Cmd(
        names={"load-script"}
)
public class LoadScript extends ICommand {
    ScriptManager scriptManager = Main.get().getScriptManager();
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /load-script <scriptName>");
            return;
        }

        try {
            String scriptName = args[0];
            scriptManager.loadScript(scriptName);
            sender.sendMessage("Loaded script: " + scriptName);
        }catch(Exception e) {
            sender.sendMessage("Error loading script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
