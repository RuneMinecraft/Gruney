package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.ErrorCode;
import net.runemc.plugin.scripting.Script;
import net.runemc.plugin.scripting.ScriptManager;
import net.runemc.utils.Locale;
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
            if (!Main.get().bindings().hasScript(scriptPath)) {
                sender.sendMessage("That script isn't loaded!");
                return;
            }
            Script script = Main.get().bindings().getScript(scriptPath);
            int result = script.unload();
            Main.get().bindings().removeScript(scriptPath);

            player().sendMessage(Locale.TRANSLATE_SCRIPT_ERROR(result) + "(" + result + ")");
        }catch(Exception e) {
            sender.sendMessage("Error unloading script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}