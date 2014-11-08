package net.frozenorb.foxtrot.command.objects;

import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/2/2014.
 */
public abstract class ParamTransformer {

    public abstract Object transform(Player sender, String source);

}