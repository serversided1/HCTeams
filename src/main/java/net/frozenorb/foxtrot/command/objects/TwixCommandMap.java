package net.frozenorb.foxtrot.command.objects;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.CommandHandler;
import net.frozenorb.mBasic.CommandSystem.Commands.Freeze;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class TwixCommandMap extends SimpleCommandMap {

    public TwixCommandMap(Server server) {
        super(server);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String typedMessage) {
        try {
            // In case the Console is trying to tab complete,
            // we just give them an empty list.
            // We do this instead of returning up above as this still allows the Console to
            // tab complete vanilla commands
            // (even though I don't ever see the Console tab completing)
            Bukkit.broadcastMessage("Got a tab complete for " + sender.getName());
            List<String> completionList = sender instanceof Player ? customTabComplete((Player) sender, typedMessage) : new ArrayList<>();

            // Merge the vanilla completions (IE those of the main SimpleCommandMap)
            // with ours.
            List<String> vanillaCompletionList = super.tabComplete(sender, typedMessage);

            // Tab completers are weird, so returning null is technically valid.
            // Oh well.
            if (vanillaCompletionList != null) {
                // Merge the vanilla completions with ours.
                completionList.addAll(vanillaCompletionList);
            }

            // Sort them by length.
            // This is sort of a workaround for the stupid 'w' bug,
            // but also something which is sort of nice,
            // instead of being given the results in an arbitrary order.
            completionList.sort(new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                    // It's okay to do this,
                    // Java Comparators are okay with non (-1 | 0 | 1) results
                    // instead, any (- | 0 | +) value is fine.
                    return (o2.length() - o1.length());
                }

            });

            return (completionList);
        } catch (RuntimeException e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
            return (new ArrayList<>());
        }
    }

    public List<String> customTabComplete(Player player, String completing) {
        List<String> completions = new ArrayList<>();

        Bukkit.broadcastMessage("Got a custom complete for " + player.getName());

        CommandLoop:
        for (CommandData command : CommandHandler.getCommands()) {
            if (!command.canAccess(player)) {
                continue;
            }

            for (String commandAlias : command.getNames()) {
                if (StringUtil.startsWithIgnoreCase(commandAlias.trim(), completing.trim()) || StringUtil.startsWithIgnoreCase(completing.trim(), commandAlias.trim())) {
                    if (completing.length() < commandAlias.length()) {
                        Bukkit.broadcastMessage("Completing " + commandAlias + " as a command.");
                        // Complete the command
                        completions.add("/" + commandAlias.toLowerCase());
                    } else if (completing.toLowerCase().startsWith(commandAlias.toLowerCase() + " ") && command.getParameters().size() > 0) {
                        Bukkit.broadcastMessage("Completing " + commandAlias + " as a param.");
                        // Complete the params
                        int paramIndex = (completing.split(" ").length - commandAlias.split(" ").length);

                        // If they didn't hit space, complete the param before it.
                        if (paramIndex == command.getParameters().size() || !completing.endsWith(" ")) {
                            paramIndex = paramIndex - 1;
                        }

                        if (paramIndex < 0) {
                            paramIndex = 0;
                        }

                        ParamData paramData = command.getParameters().get(paramIndex);
                        String[] params = completing.split(" ");

                        for (String completion : CommandHandler.tabCompleteParameter(player, completing.endsWith(" ") ? "" : params[params.length - 1], paramData.getParameterClass())) {
                            completions.add(completion);
                        }

                        break CommandLoop;
                    } else {
                        Bukkit.broadcastMessage("Completing " + commandAlias + " as a subcommand.");
                        String halfSplitString = commandAlias.toLowerCase().replaceFirst(commandAlias.split(" ")[0].toLowerCase(), "").trim();
                        String[] splitString = halfSplitString.split(" ");
                        completions.add(splitString[splitString.length - 1].trim());
                    }
                }
            }
        }

        return (completions);
    }

}