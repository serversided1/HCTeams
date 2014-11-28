package net.frozenorb.foxtrot.command.objects;

import org.bukkit.command.CommandSender;

/**
 * Created by macguy8 on 11/2/2014.
 */
public abstract class ParamTransformer<T> {

    public abstract T transform(CommandSender sender, String source);

}