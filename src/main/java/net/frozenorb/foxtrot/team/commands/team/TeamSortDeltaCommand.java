package net.frozenorb.foxtrot.team.commands.team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TeamSortDeltaCommand {

    public static final float KB_MAX = 0.5F;
    public static final float KB_MIN = -0.5F;

    @Command(names={ "team sortdelta" }, permission="op")
    public static void teamSortDelta(Player sender, @Param(name="team") Player player, @Param(name="delta") float mod) {
        if (mod > KB_MAX) {
            sender.sendMessage(ChatColor.RED + "The modifier you entered (" + mod + ") was greater than the max (" + KB_MAX + ") - it has been reduced.");
            mod = KB_MAX;
        }

        if (mod < KB_MIN) {
            sender.sendMessage(ChatColor.RED + "The modifier you entered (" + mod + ") was lower than the min (" + KB_MIN + ") - it has been increased.");
            mod = KB_MIN;
        }

        ((CraftPlayer) player).getHandle().knockbackReduction = mod;
        sender.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + "'s sort delta is " + ChatColor.LIGHT_PURPLE + mod + ChatColor.YELLOW + ". (Default is 0.2)");
    }

}