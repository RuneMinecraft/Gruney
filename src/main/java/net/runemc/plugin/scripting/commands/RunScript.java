package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.ErrorCode;
import net.runemc.plugin.scripting.Script;
import net.runemc.utils.Locale;
import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import org.bukkit.command.CommandSender;

@Cmd(
        names={"execute-script"}
)
public class RunScript extends ICommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /execute-script <scriptName>");
            return;
        }
        final long startTime = System.currentTimeMillis();

        try {
            String scriptPath = args[0];
            Script script = new Script(scriptPath, Main.get().bindings().sharedBindings());
            int result = script.execute();

            player().sendMessage(Locale.TRANSLATE_SCRIPT_ERROR(result) + "(" + result + ")");
        }catch(Exception e) {
            sender.sendMessage("Error executing script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
