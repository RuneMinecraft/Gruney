package net.runemc.plugin.commands;

import net.runemc.utils.command.Cmd;
import net.runemc.utils.command.ICommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@Cmd(
        names = {"flyspeed", "speed"},
        perms = "rune.admin"
)
public class FlySpeed extends ICommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            return;
        }

        if (!(args.length > 0)){
            return;
        }

        try {
            int speed = Integer.parseInt(args[0]);
            if (speed < 1 || speed > 10) {
                player().sendMessage("§cSpeed must be between 1 and 10.");
                return;
            }

            float normalizedSpeed = speed / 10.0f;

            // Set the fly speed
            if (player().getGameMode() == GameMode.CREATIVE.CREATIVE || player().getAllowFlight()) {
                player().setFlySpeed(normalizedSpeed);
                player().sendMessage("§aFly speed set to: " + speed);
            } else {
                player().sendMessage("§cYou must be in Creative mode or have flight enabled to set fly speed.");
            }

        } catch (NumberFormatException e) {
            player().sendMessage("§cInvalid number. Please enter a value between 1 and 10.");
        }



    }
}
