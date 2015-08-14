package net.frozenorb.foxtrot.conquest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ConquestCapzone {

    UFO(ChatColor.DARK_GREEN, "UFO"),
    METEOR(ChatColor.YELLOW, "Meteor"),
    SHUTTLE(ChatColor.BLUE, "Shuttle"),
    COMMAND(ChatColor.RED, "Command");

    @Getter private ChatColor color;
    @Getter private String name;

}