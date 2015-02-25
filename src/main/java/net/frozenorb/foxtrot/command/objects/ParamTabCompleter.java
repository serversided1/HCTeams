package net.frozenorb.foxtrot.command.objects;

import org.bukkit.entity.Player;

import java.util.List;

public abstract class ParamTabCompleter {

    public abstract List<String> tabComplete(Player sender, String source);

}