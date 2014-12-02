package net.frozenorb.foxtrot.imagemessage.commands.imagemessage;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.imagemessage.ImageMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 12/2/2014.
 */
public class ImageMessageCommand {

    @Command(names={ "ImageMessage List", "IM List" }, permissionNode="foxtrot.imagemessage")
    public static void imageMessageList(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "Viewing available image message types...");

        for (File file : new File("ascii-art").listFiles()) {
            sender.sendMessage(ChatColor.YELLOW + "- " + file.getName().split("\\.")[0]);
        }
    }

    @Command(names={ "ImageMessage Broadcast", "IM Broadcast", "ImageMessage BC", "IM BC" }, permissionNode="foxtrot.imagemessage")
    public static void imageMessageBroadcast(Player sender, @Param(name="Image") String image, @Param(name="Message", wildcard=true) String message) {
        List<String> messages = new ArrayList<String>();

        messages.add(" ");
        messages.add(" ");

        for (String messageSplit : message.split("\\|")) {
            messages.add(messageSplit);
        }

        new ImageMessage(image).appendText(messages.toArray(new String[messages.size()])).broadcast();
    }

}