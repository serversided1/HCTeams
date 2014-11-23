package net.frozenorb.foxtrot.command;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.command.objects.*;
import net.minecraft.server.v1_7_R3.CommandSeed;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class CommandHandler implements Listener {

    //***********************//

    private static List<CommandData> commands = new ArrayList<CommandData>();
    private static Map<Class<?>, ParamTransformer> parameterTransformers = new HashMap<Class<?>, ParamTransformer>();
    private static Map<Class<?>, ParamTabCompleter> parameterTabCompleters = new HashMap<Class<?>, ParamTabCompleter>();

    //***********************//

    public static void init() {
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new CommandHandler(), FoxtrotPlugin.getInstance());

        // Run this on a delay so everything is registered.
        // Not really needed, but it's nice to play it safe.
        new BukkitRunnable() {

            public void run() {
                try {
                    // Command map field (we have to use reflection to get this)
                    Field currentCommandMap = FoxtrotPlugin.getInstance().getServer().getClass().getDeclaredField("commandMap");

                    currentCommandMap.setAccessible(true);

                    // 'Old' command map
                    Object currentCommandMapObject = currentCommandMap.get(FoxtrotPlugin.getInstance().getServer());
                    TwixCommandMap commandMap = new TwixCommandMap(FoxtrotPlugin.getInstance().getServer());

                    Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");

                    knownCommands.setAccessible(true);

                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(knownCommands, knownCommands.getModifiers() & ~Modifier.FINAL);

                    knownCommands.set(commandMap, knownCommands.get(currentCommandMapObject));

                    currentCommandMap.set(FoxtrotPlugin.getInstance().getServer(), commandMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.runTaskLater(FoxtrotPlugin.getInstance(), 5L);

        (new CommandRegistrar()).register();

        registerTransformer(int.class, new ParamTransformer() {

            @Override
            public Object transform(CommandSender sender, String source) {
                try {
                    return (Integer.valueOf(source));
                } catch (NumberFormatException exception) {
                    sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
                    return (null);
                }
            }

        });

        registerTransformer(float.class, new ParamTransformer() {

            @Override
            public Object transform(CommandSender sender, String source) {
                try {
                    return (Float.valueOf(source));
                } catch (NumberFormatException exception) {
                    sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
                    return (null);
                }
            }

        });

        registerTransformer(boolean.class, new ParamTransformer() {

            @Override
            public Object transform(CommandSender sender, String source) {
                try {
                    return (Boolean.valueOf(source));
                } catch (NumberFormatException exception) {
                    sender.sendMessage(ChatColor.RED + source + " is not a valid boolean.");
                    return (null);
                }
            }

        });

        registerTabCompleter(boolean.class, new ParamTabCompleter() {

            public List<String> tabComplete(Player sender, String source) {
                List<String> completions = new ArrayList<String>();

                for (String string : new String[]{ "true", "false" }) {
                    if (StringUtils.startsWithIgnoreCase(string, source)) {
                        completions.add(string);
                    }
                }

                return (completions);
            }

        });

        registerTransformer(Player.class, new ParamTransformer() {

            @Override
            public Object transform(CommandSender sender, String source) {
                if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
                    return (sender);
                }

                Player player = FoxtrotPlugin.getInstance().getServer().getPlayer(source);

                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "No player with the name " + source + " found.");
                    return (null);
                }

                return (player);
            }

        });

        registerTabCompleter(Player.class, new ParamTabCompleter() {

            public List<String> tabComplete(Player sender, String source) {
                List<String> completions = new ArrayList<String>();

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                        completions.add(player.getName());
                    }
                }

                return (completions);
            }

        });

        registerTransformer(OfflinePlayer.class, new ParamTransformer() {

            @Override
            public Object transform(CommandSender sender, String source) {
                if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
                    return (sender);
                }

                return (FoxtrotPlugin.getInstance().getServer().getOfflinePlayer(source));
            }

        });

        registerTabCompleter(OfflinePlayer.class, new ParamTabCompleter() {

            public List<String> tabComplete(Player sender, String source) {
                List<String> completions = new ArrayList<String>();

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                        completions.add(player.getName());
                    }
                }

                return (completions);
            }

        });

        registerTransformer(World.class, new ParamTransformer() {

            @Override
            public Object transform(CommandSender sender, String source) {
                World world = FoxtrotPlugin.getInstance().getServer().getWorld(source);

                if (world == null) {
                    sender.sendMessage(ChatColor.RED + "No world with the name " + source + " found.");
                    return (null);
                }

                return (world);
            }

        });

        registerTabCompleter(World.class, new ParamTabCompleter() {

            public List<String> tabComplete(Player sender, String source) {
                List<String> completions = new ArrayList<String>();

                for (World world : FoxtrotPlugin.getInstance().getServer().getWorlds()) {
                    if (StringUtils.startsWithIgnoreCase(world.getName(), source)) {
                        completions.add(world.getName());
                    }
                }

                return (completions);
            }

        });

        registerClass(CommandHandler.class);
    }

    //***********************//

    @Command(names={"ListCommands"}, permissionNode="foxtrot.listcommands")
    public static void listCommands(CommandSender sender) {
        for (CommandData command : commands) {
            StringBuilder stringBuilder = new StringBuilder();

            for (ParamData param : command.getParameters()) {
                stringBuilder.append(param.getDefaultValue().equalsIgnoreCase("") ? "<" : "[").append(param.getName());

                if (!param.getParameterClass().equals(String.class)) {
                    stringBuilder.append(" (").append(param.getParameterClass().getSimpleName()).append(")");
                }

                if (param.isWildcard()) {
                    stringBuilder.append("*");
                }

                stringBuilder.append(param.getDefaultValue().equalsIgnoreCase("") ? ">" : "]").append(" ");
            }

            sender.sendMessage(command.getPermissionNode() + ChatColor.YELLOW + " /" + command.getName() + " " + stringBuilder.toString());
        }

        for (Map.Entry<Class<?>, ParamTransformer> entry : parameterTransformers.entrySet()) {
            if (parameterTabCompleters.containsKey(entry.getKey())) {
                continue;
            }

            sender.sendMessage(ChatColor.RED + entry.getKey().getSimpleName() + " is an accepted parameter type, but doesn't have a tab completer registered.");
        }
    }

    //***********************//

    public static void registerTransformer(Class<?> transforms, ParamTransformer transformer) {
        parameterTransformers.put(transforms, transformer);
    }

    public static void registerTabCompleter(Class<?> tabCompletes, ParamTabCompleter tabCompleter) {
        parameterTabCompleters.put(tabCompletes, tabCompleter);
    }

    public static void registerClass(Class<?> registeredClass) {
        for (Method method : registeredClass.getMethods()) {
            if (method.getAnnotation(Command.class) != null) {
                registerMethod(method);
            }
        }
    }

    public static void registerMethod(Method method) {
        Command command = method.getAnnotation(Command.class);
        List<ParamData> paramData = new ArrayList<ParamData>();

        for (int i = 1; i < method.getParameterTypes().length; i++) {
            Param param = null;

            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                    break;
                }
            }

            if (param != null) {
                paramData.add(new ParamData(method.getParameterTypes()[i], param));
            } else {
                FoxtrotPlugin.getInstance().getLogger().warning(method.getName() + " is (somewhere) missing a @Param annotation.");
                return;
            }
        }

        commands.add(new CommandData(method, command, paramData, !method.getParameterTypes()[0].getClass().equals(Player.class)));

        Collections.sort(commands, new Comparator<CommandData>() {

            @Override
            public int compare(CommandData o1, CommandData o2) {
                return (Integer.valueOf(o2.getName().length()).compareTo(o1.getName().length()));
            }

        });
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String[] args = new String[] { };
        CommandData found = null;

        CommandLoop:
        for (CommandData commandData : commands) {
            for (String alias : commandData.getNames()) {
                String messageString = event.getMessage().substring(1).toLowerCase() + " ";
                String aliasString = alias.toLowerCase() + " ";

                if (messageString.startsWith(aliasString)) {
                    found = commandData;

                    if (event.getMessage().length() > alias.length() + 2) {
                        args = (event.getMessage().substring(alias.length() + 2)).split(" ");
                    }

                    break CommandLoop;
                }
            }
        }

        if (found == null) {
            return;
        }

        event.setCancelled(true);

        if (found.getPermissionNode().equals("op")) {
            if (!event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
                return;
            }
        } else if (!found.getPermissionNode().equals("")) {
            if (!event.getPlayer().hasPermission(found.getPermissionNode())) {
                event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
                return;
            }
        }

        ArrayList<Object> transformedParams = new ArrayList<Object>();

        transformedParams.add(event.getPlayer()); // Add the sender

        for (int paramIndex = 0; paramIndex < found.getParameters().size(); paramIndex++) {
            ParamData param = found.getParameters().get(paramIndex);
            String passedParam = (paramIndex < args.length ? args[paramIndex] : param.getDefaultValue()).trim();

            if (paramIndex >= args.length && (param.getDefaultValue() == null || param.getDefaultValue().equals(""))) {
                StringBuilder stringBuilder = new StringBuilder();

                for (ParamData paramHelp : found.getParameters()) {
                    stringBuilder.append(paramHelp.getDefaultValue().equalsIgnoreCase("") ? "<" : "[").append(paramHelp.getName());
                    stringBuilder.append(paramHelp.getDefaultValue().equalsIgnoreCase("") ? ">" : "]").append(" ");
                }

                event.getPlayer().sendMessage(ChatColor.RED + "Usage: /" + found.getName() + " " + stringBuilder.toString());
                return;
            }

            if (param.isWildcard() && !passedParam.trim().equals(param.getDefaultValue().trim())) {
                passedParam = toString(args, paramIndex);
            }

            Object result = transformParameter(event.getPlayer(), passedParam, param.getParameterClass());

            if (result == null) {
                return;
            }

            transformedParams.add(result);

            if (param.isWildcard()) {
                break;
            }
        }

        try {
            found.getMethod().invoke(null, transformedParams.toArray(new Object[transformedParams.size()]));
        } catch (Exception e) {
            event.getPlayer().sendMessage(ChatColor.RED + "It appears there was some issues processing your command...");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        String[] args = new String[] { };
        CommandData found = null;

        CommandLoop:
        for (CommandData commandData : commands) {
            for (String alias : commandData.getNames()) {
                String messageString = event.getCommand().toLowerCase() + " ";
                String aliasString = alias.toLowerCase() + " ";

                if (messageString.startsWith(aliasString)) {
                    found = commandData;

                    if (event.getCommand().length() > alias.length() + 1) {
                        args = (event.getCommand().substring(alias.length() + 1)).split(" ");
                    }

                    break CommandLoop;
                }
            }
        }

        if (found == null) {
            return;
        }

        event.setCommand("");

        if (!found.isConsole()) {
            event.getSender().sendMessage(ChatColor.RED + "This command does not support execution from the console.");
            return;
        }

        ArrayList<Object> transformedParams = new ArrayList<Object>();

        transformedParams.add(event.getSender()); // Add the sender

        for (int paramIndex = 0; paramIndex < found.getParameters().size(); paramIndex++) {
            ParamData param = found.getParameters().get(paramIndex);
            String passedParam = (paramIndex < args.length ? args[paramIndex] : param.getDefaultValue()).trim();

            if (paramIndex >= args.length && (param.getDefaultValue() == null || param.getDefaultValue().equals(""))) {
                StringBuilder stringBuilder = new StringBuilder();

                for (ParamData paramHelp : found.getParameters()) {
                    stringBuilder.append(paramHelp.getDefaultValue().equalsIgnoreCase("") ? "<" : "[").append(paramHelp.getName());
                    stringBuilder.append(paramHelp.getDefaultValue().equalsIgnoreCase("") ? ">" : "]").append(" ");
                }

                event.getSender().sendMessage(ChatColor.RED + "Usage: /" + found.getName() + " " + stringBuilder.toString());
                return;
            }

            if (param.isWildcard() && !passedParam.trim().equals(param.getDefaultValue().trim())) {
                passedParam = toString(args, paramIndex);
            }

            Object result = transformParameter(event.getSender(), passedParam, param.getParameterClass());

            if (result == null) {
                return;
            }

            transformedParams.add(result);

            if (param.isWildcard()) {
                break;
            }
        }

        try {
            found.getMethod().invoke(null, transformedParams.toArray(new Object[transformedParams.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object transformParameter(CommandSender sender, String parameter, Class<?> transformTo) {
        if (transformTo.equals(String.class)) {
            return (parameter);
        }

        return (parameterTransformers.get(transformTo).transform(sender, parameter));
    }

    public static List<String> tabCompleteParameter(Player sender, String parameter, Class<?> transformTo) {
        if (!parameterTabCompleters.containsKey(transformTo)) {
            return (new ArrayList<String>());
        }

        return (parameterTabCompleters.get(transformTo).tabComplete(sender, parameter));
    }

    public static List<CommandData> getCommands() {
        return (commands);
    }

    public static CommandData getCommand(String name) {
        for (CommandData commandData : commands) {
            for (String alias : commandData.getNames()) {
                if (alias.equalsIgnoreCase(name)) {
                    return (commandData);
                }
            }
        }

        return (null);
    }

    public static String toString(String[] args, int start) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int arg = start; arg < args.length; arg++) {
            stringBuilder.append(args[arg]).append(" ");
        }

        return (stringBuilder.toString().trim());
    }


}