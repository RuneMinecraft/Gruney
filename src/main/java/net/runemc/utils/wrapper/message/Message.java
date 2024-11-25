package net.runemc.utils.wrapper.message;

import net.runemc.utils.Logger;
import net.runemc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Message implements Utils {
    private final MessageType type;
    private final Set<CommandSender> players;
    private final String[] messages;

    private Message(MessageType type, Set<CommandSender> input, String... messages) {
        this.type = type;
        this.messages = messages;
        this.players = new HashSet<>();
    }

    public static Message create(MessageType type, CommandSender player, String ... messages) {
        return create(type, Collections.singleton(player).toArray(CommandSender[]::new), messages);
    }
    public static Message create(CommandSender player, String ... messages) {
        return Message.create(MessageType.NORMAL, player, messages);
    }
    public static Message create(MessageType type, CommandSender[] players, String ... messages) {
        return new Message(type, Arrays.stream(players).collect(Collectors.toSet()), messages);
    }
    public static Message create(CommandSender[] players, String ... messages) {
        return create(MessageType.NORMAL, players, messages);
    }

    public static Message broadcast(MessageType type, String ... messages) {
        return create(type, Bukkit.getOnlinePlayers().toArray(CommandSender[]::new), messages);
    }
    public static Message broadcast(String ... messages) {
        return broadcast(MessageType.NORMAL, messages);
    }

    public MessageType type() {
        return this.type;
    }
    public Set<CommandSender> players() {
        return this.players;
    }
    public String[] messages() {
        return this.messages;
    }

    public void send() {
        if (messages == null) {
            Logger.log("Failed to send a message. 'String[] messages' is null.");
            return;
        }

        for (CommandSender player : this.players()) {
            Arrays.stream(this.messages()).toList().forEach(message -> player.sendMessage(Colour(this.type() + message)));
        }
    }
    public void send(boolean log) {
        this.send();
        if (log) {
            Logger.log(this.toString());
        }
    }

    @Override
    public String toString() {
        StringBuilder playersNames = new StringBuilder();
        for (CommandSender player : players) {
            playersNames.append(player.getName());
            playersNames.append(", ");
        }

        if (!playersNames.isEmpty()) {
            playersNames.setLength(playersNames.length() - 2);
        }

        return "new Message() {\n" +
                "\ttype: " + type.name() + ",\n" +
                "\tplayers: " + playersNames + ",\n" +
                "\tmessages: " + Arrays.toString(messages) +
                "\n}";
    }
}