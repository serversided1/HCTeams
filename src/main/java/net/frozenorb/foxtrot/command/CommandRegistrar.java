package net.frozenorb.foxtrot.command;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

public class CommandRegistrar {

	private SimpleCommandMap commandMap;

	/**
	 * Creates a new instance of {@link net.frozenorb.Utilities.CommandSystem.CommandLoader}, and loads all commands
	 * from a package
	 */
	public CommandRegistrar() {
		try {
			commandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Loads all commands from a package
	 * <p>
	 * Scans all top level classes in a package, attempts to register classes
	 * that implement {@link CommandExecutor}
	 * 
	 * @param packageName
	 *            the package to scan
	 */
	public void loadCommandsFromPackage(String packageName) {
		System.out.println("Loading commands from package: " + packageName);
		for (Class<?> clazz : getClassesInPackage(packageName)) {
			System.out.println(clazz.getName() + "\n\n");

			if (BaseCommand.class.isAssignableFrom(clazz)) {
				try {
					BaseCommand executor = (BaseCommand) clazz.newInstance();
					registerCommand(executor.getCommandName(), executor);
				}
				catch (Exception e) {
					e.printStackTrace();
					System.out.print(String.format("Could not load '%s', see: %s", clazz.getSimpleName(), e.getMessage()));
				}
			}
		}
	}

	public static ArrayList<Class<?>> getClassesInPackage(String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		CodeSource codeSource = FoxtrotPlugin.getInstance().getClass().getProtectionDomain().getCodeSource();
		URL resource = codeSource.getLocation();
		String relPath = pkgname.replace('.', '/');
		String resPath = resource.getPath().replace("%20", " ");
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
		JarFile jFile;
		try {
			jFile = new JarFile(jarPath);
		}
		catch (IOException e) {
			throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
		}
		Enumeration<JarEntry> entries = jFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			String className = null;
			if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
				className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
			}
			if (className != null) {
				Class<?> c = null;
				try {
					c = Class.forName(className);
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				if (c != null)
					classes.add(c);
			}
		}
		try {
			jFile.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * Registers a new command
	 * 
	 * @param cmd
	 *            the command name
	 * @param executor
	 *            the command executor (or subclass) to register the command to
	 * @throws Exception
	 *             if we get any reflection issues
	 */
	public void registerCommand(String cmd, BaseCommand executor)
			throws Exception {
		PluginCommand command = Bukkit.getServer().getPluginCommand(cmd.toLowerCase());
		if (command == null) {
			Constructor<?> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			command = (PluginCommand) constructor.newInstance(cmd, FoxtrotPlugin.getInstance());
		}
		command.setExecutor(executor);
		List<String> list = Arrays.asList(executor.aliases);
		command.setAliases(list);
		if (command.getAliases() != null) {
			for (String alias : command.getAliases())
				unregisterCommand(alias);
		}
		try {
			Field field = executor.getClass().getDeclaredField("description");
			field.setAccessible(true);
			if (field != null && field.get(executor) instanceof String)
				command.setDescription(ChatColor.translateAlternateColorCodes('&', (String) field.get(executor)));
		}
		catch (Exception ex) {}
		commandMap.register(cmd, command);
	}

	/**
	 * Unregisters a commend from the command map
	 * 
	 * @param name
	 *            the name of the command
	 */
	@SuppressWarnings("unchecked")
	public void unregisterCommand(String name) {
		try {
			Field known = SimpleCommandMap.class.getDeclaredField("knownCommands");
			Field alias = SimpleCommandMap.class.getDeclaredField("aliases");
			known.setAccessible(true);
			alias.setAccessible(true);
			Map<String, Command> knownCommands = (Map<String, Command>) known.get(commandMap);
			Set<String> aliases = (Set<String>) alias.get(commandMap);
			knownCommands.remove(name.toLowerCase());
			aliases.remove(name.toLowerCase());
		}
		catch (Exception ex) {

		}
	}

	/**
	 * Registers all of the commands to their respective classes
	 */
	public void register() {
		loadCommandsFromPackage("net.frozenorb.foxtrot.command.commands");
	}
}
