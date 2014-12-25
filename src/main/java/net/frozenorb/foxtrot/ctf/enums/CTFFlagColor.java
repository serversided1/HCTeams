package net.frozenorb.foxtrot.ctf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@AllArgsConstructor
public enum CTFFlagColor {

    RED("Red", ChatColor.RED, DyeColor.RED),
    BLUE("Blue", ChatColor.BLUE, DyeColor.BLUE),
    YELLOW("Yellow", ChatColor.YELLOW, DyeColor.YELLOW),
    GREEN("Green", ChatColor.GREEN, DyeColor.LIME);

    @Getter String name;
    @Getter ChatColor chatColor;
    @Getter DyeColor dyeColor;

}