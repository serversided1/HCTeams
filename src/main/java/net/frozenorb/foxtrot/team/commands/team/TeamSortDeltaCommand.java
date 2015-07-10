package net.frozenorb.foxtrot.team.commands.team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TeamSortDeltaCommand {

    @Command(names={ "team sortdelta" }, permissionNode="op")
    public static void teamSortDelta(Player sender, @Parameter(name="team") Player player, @Parameter(name="delta") float mod) {
        ((CraftPlayer) player).getHandle().knockbackReduction = mod;
        sender.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + "'s sort delta is " + ChatColor.LIGHT_PURPLE + mod + ChatColor.YELLOW + ". (Default is 0.2)");
    }

}