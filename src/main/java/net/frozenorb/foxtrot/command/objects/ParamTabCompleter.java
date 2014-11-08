package net.frozenorb.foxtrot.command.objects;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by macguy8 on 11/2/2014.
 */
public abstract class ParamTabCompleter {

    public abstract List<String> tabComplete(Player sender, String source);

}