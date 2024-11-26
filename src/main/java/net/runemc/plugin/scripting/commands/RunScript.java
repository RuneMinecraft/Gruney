package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.ErrorCode;
import net.runemc.plugin.scripting.Script;
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
            if (!Main.get().bindings().hasScript(scriptPath)) {
                sender.sendMessage("You need to load the script first!");
                return;
            }
            Script script = Main.get().bindings().getScript(scriptPath);
            int result = script.execute();

            switch (result) {
                case ErrorCode.SUCCESS -> sender.sendMessage(Colour("&aSuccessfully executing the script &f" + scriptPath + "&a! &7&o(Took " + (System.currentTimeMillis() - startTime) + "ms)"));
                case ErrorCode.ERROR_DURING_EXECUTION -> sender.sendMessage(Colour("&cAn unexpected error has occured whilst loading the executing &f"+scriptPath));
                default -> sender.sendMessage(Colour("&cFailed to executing script: &f" + scriptPath));
            }
        }catch(Exception e) {
            sender.sendMessage("Error executing script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
