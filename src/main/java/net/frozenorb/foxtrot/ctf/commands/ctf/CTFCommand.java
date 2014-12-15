package net.frozenorb.foxtrot.ctf.commands.ctf;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagState;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class CTFCommand {

    @Command(names={ "ctf" }, permissionNode="")
    public static void ctf(Player sender) {
        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.RED + "There isn't an active CTF game!");
            return;
        }

        for (CTFFlag flag : game.getFlags().values()) {
            Location flagLocation = flag.getLocation();
            String locationString = "";

            if (flag.getState() == CTFFlagState.CAP_POINT) {
                locationString = "At cap point";
            } else {
                locationString = "Held by " + flag.getFlagHolder().getName();
            }

            sender.sendMessage(flag.getColor().getChatColor() + flag.getColor().getName() + " Flag: " + ChatColor.WHITE + locationString + ChatColor.DARK_AQUA + " (" + flagLocation.getBlockX() + ", " + flagLocation.getBlockY() + ", " + flagLocation.getBlockZ() + ")");
        }

        sender.sendMessage("");

        Map<ObjectId, Set<CTFFlagColor>> capturedFlags = game.getCapturedFlags();

        for (Map.Entry<ObjectId, Set<CTFFlagColor>> teamCapturedFlagsEntry : capturedFlags.entrySet()) {
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(teamCapturedFlagsEntry.getKey());

            if (team != null) {
                StringBuilder capturedBuilder = new StringBuilder();

                for (CTFFlagColor flagColor : CTFFlagColor.values()) {
                    if (teamCapturedFlagsEntry.getValue().contains(flagColor)) {
                        capturedBuilder.append(ChatColor.GREEN.toString()).append(flagColor.getName()).append(" ");
                    } else {
                        capturedBuilder.append(ChatColor.GRAY.toString()).append(flagColor.getName()).append(" ");
                    }
                }

                sender.sendMessage(ChatColor.GOLD + team.getName() + ": " + ChatColor.WHITE + teamCapturedFlagsEntry.getValue().size() + "/" + CTFFlagColor.values().length + " " + capturedBuilder.toString().trim());
            }
        }
    }

}