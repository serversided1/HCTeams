package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamInfoCommand {

    @Command(names={ "team info", "t info", "f info", "faction info", "fac info", "team who", "t who", "f who", "faction who", "fac who", "team show", "t show", "f show", "faction show", "fac show", "team i", "t i", "f i", "faction i", "fac i" }, permission="")
    public static void teamInfo(final Player sender, @Param(name="team", defaultValue="self", tabCompleteFlags={ "noteams", "players" }) final Team team) {
        new BukkitRunnable() {

            public void run() {
                Team exactPlayerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(team.getName()));

                if (exactPlayerTeam != null && exactPlayerTeam != team) {
                    exactPlayerTeam.sendTeamInfo(sender);
                }

                team.sendTeamInfo(sender);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}