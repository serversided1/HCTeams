package net.frozenorb;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.CommandHandler;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class CommandRegistrar {

    /*

    This class must be in this package.
    To iteratively load commands (and get all classes), the package we're searching (net.frozenorb.foxtrot) has to be 'lower' than this class.

     */

    public void loadCommandsFromPackage(String packageName) {
        for (Class<?> clazz : getClassesInPackage(packageName)) {
            CommandHandler.registerClass(clazz);
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
        } catch (IOException e) {
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
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (c != null) {
                    classes.add(c);
                }
            }
        }

        try {
            jFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (classes);
    }

    public void register() {
        loadCommandsFromPackage("net.frozenorb.foxtrot.command");
        loadCommandsFromPackage("net.frozenorb.foxtrot.koth");
        loadCommandsFromPackage("net.frozenorb.foxtrot.server");
        loadCommandsFromPackage("net.frozenorb.foxtrot.team");
        loadCommandsFromPackage("net.frozenorb.foxtrot.citadel");
        loadCommandsFromPackage("net.frozenorb.foxtrot.imagemessage");
    }

}