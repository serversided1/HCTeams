package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import java.io.*;

/**
 * Created by macguy8 on 12/2/2014.
 */
public class BackupUtils {

    public static void fullBackup(FoxCallback callback) {
        FoxtrotPlugin.getInstance().getServer().dispatchCommand(FoxtrotPlugin.getInstance().getServer().getConsoleSender(), "save-off");
        FoxtrotPlugin.getInstance().getServer().dispatchCommand(FoxtrotPlugin.getInstance().getServer().getConsoleSender(), "save-all");

        try {
            Process proc = Runtime.getRuntime().exec("./fullBackup.sh");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String aux = "";

            while ((aux = stdInput.readLine()) != null) {
                System.out.println(aux);
            }

            stdInput.close();
            proc.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FoxtrotPlugin.getInstance().getServer().dispatchCommand(FoxtrotPlugin.getInstance().getServer().getConsoleSender(), "save-on");
        callback.call(null);
    }

}