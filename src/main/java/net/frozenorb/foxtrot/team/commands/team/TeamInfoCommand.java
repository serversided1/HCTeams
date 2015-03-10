package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamInfoCommand {

    @Command(names={ "team info", "t info", "f info", "faction info", "fac info", "team who", "t who", "f who", "faction who", "fac who", "team show", "t show", "f show", "faction show", "fac show", "team i", "t i", "f i", "faction i", "fac i" }, permissionNode="")
    public static void teamInfo(Player sender, @Parameter(name="team", defaultValue="self") Team target) {
        new BukkitRunnable() {

            public void run() {
                Team exactPlayerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(target.getName());

                if (exactPlayerTeam != null && exactPlayerTeam != target) {
                    exactPlayerTeam.sendTeamInfo(sender);
                }

                target.sendTeamInfo(sender);
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }

}