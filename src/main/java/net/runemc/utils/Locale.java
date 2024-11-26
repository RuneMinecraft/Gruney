package net.runemc.utils;

import org.bukkit.block.data.type.Switch;

public final class Locale {
    public static final String AUCTION_GUI_TITLE = "Auction House";
    public static final String DEFAULT_PUNISHMENT_REASON = "&cNo Reason Provided!";
    public static final String PLAYER_ONLY_COMMAND = "&cThis command can only be ran by a player!";
    public static String TRANSLATE_SCRIPT_ERROR(int code){
        switch (code){
            case 0 -> {return "§aSuccessfull";}
            case 1 -> {return "§eScript Already Loaded";}
            case 2 -> {return "§eScript not loaded";}
            case 3 -> {return "§cError During Loading";}
            case 4 -> {return "§cError During Execution";}
            case 5 -> {return "§cError During Unloading";}
            default -> {return "§c§lCODE NOT FOUND, YOU FUCKED SOMETHING UP DUMBASS :D";}
        }
    }
}
