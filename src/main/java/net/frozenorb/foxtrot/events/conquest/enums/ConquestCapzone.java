package net.frozenorb.foxtrot.events.conquest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ConquestCapzone {

    GREEN(ChatColor.DARK_GREEN, "Green"),
    YELLOW(ChatColor.YELLOW, "Yellow"),
    BLUE(ChatColor.BLUE, "Blue"),
    RED(ChatColor.RED, "Red");

    @Getter private ChatColor color;
    @Getter private String name;

}