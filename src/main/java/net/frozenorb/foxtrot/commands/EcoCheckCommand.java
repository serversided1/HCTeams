package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EcoCheckCommand {

    @Command(names={ "ecocheck" }, permissionNode="op")
    public static void ecoCheck(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (isBad(team.getBalance())) {
                sender.sendMessage(ChatColor.YELLOW + "Team: " + ChatColor.WHITE + team.getName());
            }
        }

        try {
            Field balancesField = Basic.get().getEconomyManager().getClass().getDeclaredField("balances");
            balancesField.setAccessible(true);
            HashMap<String, Double> balances = (HashMap<String, Double>) balancesField.get(Basic.get().getEconomyManager());

            for (Map.Entry<String, Double> balanceEntry  : balances.entrySet()) {
                if (isBad(balanceEntry.getValue().doubleValue())) {
                    sender.sendMessage(ChatColor.YELLOW + "Player: " + ChatColor.WHITE + balanceEntry.getKey());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isBad(double bal) {
        return (Double.isNaN(bal) || Double.isInfinite(bal) || bal > 1_000_000D);
    }

}