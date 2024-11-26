package net.runemc.plugin.scripting.commands;

import net.runemc.plugin.Main;
import net.runemc.plugin.scripting.ErrorCode;
import net.runemc.plugin.scripting.Script;
import net.runemc.plugin.scripting.ScriptBindings;
import net.runemc.plugin.scripting.ScriptManager;
import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import net.runemc.utils.wrapper.message.Message;
import org.bukkit.command.CommandSender;

import java.util.Map;

@Cmd(
        names={"load-script"}
)
public class LoadScript extends ICommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /load-script <scriptName>");
            return;
        }
        final long startTime = System.currentTimeMillis();

        try {
            String scriptPath = args[0];
            Script script = new Script(scriptPath, Main.get().bindings().sharedBindings());
            int result = script.load();
            Main.get().bindings().addScript(scriptPath, script);

            switch (result) {
                case ErrorCode.SUCCESS -> sender.sendMessage(Colour("&aSuccessfully loaded the script &f" + scriptPath + "&a! &7&o(Took " + (System.currentTimeMillis() - startTime) + "ms)"));
                case ErrorCode.SCRIPT_ALREADY_LOADED -> sender.sendMessage(Colour("&f"+scriptPath+" &cis already loaded!"));
                case ErrorCode.ERROR_DURING_LOADING -> sender.sendMessage(Colour("&cAn unexpected error has occured whilst loading the script &f"+scriptPath));
                default -> sender.sendMessage(Colour("&cFailed to load script: &f" + scriptPath));
            }
        }catch(Exception e) {
            sender.sendMessage("Error loading script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
