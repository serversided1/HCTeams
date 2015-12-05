package net.frozenorb.foxtrot.gui.button;

import lombok.AllArgsConstructor;
import net.frozenorb.Utilities.Interfaces.Callback;
import net.frozenorb.foxtrot.gui.menu.ConfirmMenu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.ForceDisbandCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamDisbandCommand;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DisbandTeamButton extends Button {
    private Team team;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        new ConfirmMenu("Disband?", (b) -> {
            if (b) {
                ForceDisbandCommand.forceDisband(player, team);
            }
        }).openMenu(player);
    }

    @Override
    public String getName(Player player) {
        return "§c§lDisband Team";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.TNT;
    }
}
