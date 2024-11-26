package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.ErrorCode;
import net.runemc.plugin.scripting.Script;
import net.runemc.plugin.scripting.ScriptManager;
import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import org.bukkit.command.CommandSender;

@Cmd(
        names={"unload-script"}
)
public class UnloadScript extends ICommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /unload-script <scriptName>");
            return;
        }
        final long startTime = System.currentTimeMillis();

        try {
            String scriptPath = args[0];
            Script script = new Script(scriptPath, Main.get().bindings().sharedBindings());
            int result = script.unload();

            switch (result) {
                case ErrorCode.SUCCESS -> sender.sendMessage(Colour("&aSuccessfully unloaded the script &f" + scriptPath + "&a! &7&o(Took " + (System.currentTimeMillis() - startTime) + "ms)"));
                case ErrorCode.SCRIPT_NOT_LOADED -> sender.sendMessage(Colour("&f"+scriptPath+" &cis already unloaded!"));
                case ErrorCode.ERROR_DURING_UNLOADING -> sender.sendMessage(Colour("&cAn unexpected error has occured whilst unloaded the script &f"+scriptPath));
                default -> sender.sendMessage(Colour("&cFailed to unload script: &f" + scriptPath));
            }
        }catch(Exception e) {
            sender.sendMessage("Error unloading script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}