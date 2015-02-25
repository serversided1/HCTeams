package net.frozenorb.foxtrot.command.objects;

import org.bukkit.command.CommandSender;

public abstract class ParamTransformer<T> {

    public abstract T transform(CommandSender sender, String source);

}