package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class CSVExportCommand {

    @Command(names={ "csvexport" }, permission="op")
    public static void csvExport(Player sender) {
        try (FileWriter fileWriter = new FileWriter("export.csv")) {
            fileWriter.append("Name,HasTeam,TeamBalance,TeamSize,CoalMined,DiamondMined,EmeraldMined,FishingKitUses,FriendLives,GoldMined,IronMined,Kills,LapisMined,Playtime,RedstoneMined,SoulboundLives,Balance,Whitelisted,OP").append('\n');

            for (UUID player : Foxtrot.getInstance().getFirstJoinMap().getAllPlayers()) {
                Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                OfflinePlayer offlinePlayer = Foxtrot.getInstance().getServer().getOfflinePlayer(player);

                fileWriter.append(FrozenUUIDCache.name(player)).append(",");
                fileWriter.append(playerTeam != null ? "1" : "0").append(",");
                fileWriter.append(String.valueOf(playerTeam == null ? 0 : playerTeam.getBalance())).append(",");
                fileWriter.append(String.valueOf(playerTeam == null ? 0 : playerTeam.getSize())).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getCoalMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getDiamondMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getEmeraldMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getFishingKitMap().getUses(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getFriendLivesMap().getLives(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getGoldMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getIronMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getKillsMap().getKills(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getLapisMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getPlaytimeMap().getPlaytime(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getRedstoneMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getSoulboundLivesMap().getLives(player))).append(",");
                fileWriter.append(String.valueOf(Foxtrot.getInstance().getWrappedBalanceMap().getBalance(player))).append(",");
                fileWriter.append(String.valueOf(offlinePlayer.isWhitelisted() ? "1" : "0")).append(",");
                fileWriter.append(String.valueOf(offlinePlayer.isOp() ? "1" : "0")).append('\n');

                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage("Done!");
    }

}