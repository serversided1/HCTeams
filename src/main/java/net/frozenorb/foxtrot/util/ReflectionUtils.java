package net.frozenorb.foxtrot.util;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;

/**
 * Contains static methods for help with reflection.
 * 
 * @author Kerem Celik
 * 
 */
public class ReflectionUtils {

	private static final String PACKAGE_PREFIX = "org/bukkit/craftbukkit/v";
	private static String version = "";

	static {
		try {
			File file = new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

			ZipInputStream zis = new ZipInputStream(new FileInputStream(file));

			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {

				String name = entry.getName().replace("\\", "/");

				if (name.startsWith(PACKAGE_PREFIX)) {
					String ver = "";
					for (int t = PACKAGE_PREFIX.length(); t < name.length(); t++) {
						char c = name.charAt(t);
						if (c != '/')
							ver += c;
						else
							break;
					}
					version = "v" + ver;
					break;
				}
			}

			zis.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the current version of the internal OBC and NMS classpaths
	 * <p>
	 * 
	 * @return version
	 */
	public static String getVersion() {
		return version;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void putInPrivateStaticMap(Class clazz, String fieldName, Object key, Object value)
			throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		Map map = (Map) field.get(null);
		map.put(key, value);
		field.set(null, map);
	}

	/**
	 * Gets the classpath of the CraftPlayer classfile.
	 * 
	 * @return CraftPlayer classpath
	 */
	public static String getCraftPlayerClasspath() {
		return "org.bukkit.craftbukkit." + getVersion() + ".entity.CraftPlayer";
	}

	/**
	 * Gets the classpath of the PlayerConnection classfile.
	 * 
	 * @return PlayerConnection classpath
	 */
	public static String getPlayerConnectionClasspath() {
		return "net.minecraft.server." + getVersion() + ".PlayerConnection";
	}

	/**
	 * Gets the classpath of the EntityPlayer classfile.
	 * 
	 * @return EntityPlayer classpath
	 */
	public static String getNMSPlayerClasspath() {
		return "net.minecraft.server." + getVersion() + ".EntityPlayer";
	}

	/**
	 * Gets the classpath of the Packet classfile.
	 * 
	 * @return Packet classpath
	 */
	public static String getPacketClasspath() {
		return "net.minecraft.server." + getVersion() + ".Packet";
	}

	/**
	 * Gets the classpath of the PacketPlayOutScoreboardTeam classfile.
	 * 
	 * @return PacketPlayOutScoreboardTeam classpath
	 */
	public static String getPacketTeamClasspath() {
		return "net.minecraft.server." + getVersion() + ".PacketPlayOutScoreboardTeam";
	}

}
