package net.frozenorb.foxtrot.command;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.command.objects.*;
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
                    FoxtrotPlugin.getInstance().getBugSnag().notify(e);
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

        registerTransformer(Player.class, new ParamTransformer<Player>() {

            @Override
            public Player transform(CommandSender sender, String source) {
                if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
                    return ((Player) sender);
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
            sender.sendMessage(command.getPermissionNode() + ChatColor.YELLOW + " " + command.getUsageString());
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
                if (!Modifier.isStatic(method.getModifiers())) {
                    FoxtrotPlugin.getInstance().getLogger().warning("Method " + method.getName() + " has an @Command annotation. but isn't static.");
                    continue;
                }

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
                FoxtrotPlugin.getInstance().getLogger().warning(method.getDeclaringClass().getSimpleName() + " -> " + method.getName() + " is missing a @Param annotation.");
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
                String messageString = event.getMessage().substring(1).toLowerCase() + " "; // Chop off the slash, add a space.
                String aliasString = alias.toLowerCase() + " "; // Add a space.
                // The space is added so '/pluginslol' doesn't match '/plugins'

                if (messageString.startsWith(aliasString)) {
                    found = commandData;

                    // If there's 'space' after the command, parse args.
                    // The +2 is there to account for two things.
                    // 1) The '/' in the message that's not in the alias
                    // 2) A space after the comman if there's parameters
                    if (event.getMessage().length() > alias.length() + 2) {
                        // See above as to... why this works.
                        args = (event.getMessage().substring(alias.length() + 2)).split(" ");
                    }

                    // We break to the command loop as we have 2 for loops here.
                    break CommandLoop;
                }
            }
        }

        if (found == null) {
            return;
        }

        event.setCancelled(true);

        if (!found.canAccess(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        found.execute(event.getPlayer(), args);
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        evalCommand(event.getSender(), event.getCommand());
    }

    public static void evalCommand(CommandSender console, String command) {
        String[] args = new String[] { };
        CommandData found = null;

        CommandLoop:
        for (CommandData commandData : commands) {
            for (String alias : commandData.getNames()) {
                String messageString = command.toLowerCase() + " "; // Add a space.
                String aliasString = alias.toLowerCase() + " "; // Add a space.
                // The space is added so '/pluginslol' doesn't match '/plugins'

                if (messageString.startsWith(aliasString)) {
                    found = commandData;

                    // If there's 'space' after the command, parse args.
                    // The +1 is there to account for a space after the command if there's parameters
                    if (command.length() > alias.length() + 1) {
                        // See above as to... why this works.
                        args = (command.substring(alias.length() + 1)).split(" ");
                    }

                    // We break to the command loop as we have 2 for loops here.
                    break CommandLoop;
                }
            }
        }

        if (found == null) {
            return;
        }

        if (!found.isConsole()) {
            console.sendMessage(ChatColor.RED + "This command does not support execution from the console.");
            return;
        }

        found.execute(console, args);
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

}