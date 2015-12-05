package net.frozenorb.foxtrot.gui.button;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gui.menu.ConfirmMenu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.ForceLeaderCommand;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ChangePromotionStatusButton extends Button {

    @NonNull
    private UUID uuid;
    @NonNull
    private Team team;
    @NonNull
    private boolean promote;

    @Override
    public String getName(Player player) {

        if (promote) {
            return "§aPromote §e" + UUIDUtils.name(uuid);

        }
        return "§cDemote §e" + UUIDUtils.name(uuid);
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        if (promote) {
            lore.add("§eClick to promote §b" + UUIDUtils.name(uuid) + "§e to captain");
        } else {
            lore.add("§eClick to demote §b" + UUIDUtils.name(uuid) + "§e to member");
        }

        return lore;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {


        if (promote) {
            new ConfirmMenu("Make " + UUIDUtils.name(uuid) + " captain?", (b) -> {
                if (b) {
                    team.addCaptain(uuid);
                    player.sendMessage(ChatColor.YELLOW + UUIDUtils.name(uuid) + " has been made a captain of " + team.getName() + ".");
                }
            });


        } else {
            new ConfirmMenu("Make " + UUIDUtils.name(uuid) + " member?", (b) -> {
                if (b) {
                    team.removeCaptain(uuid);
                    player.sendMessage(ChatColor.YELLOW + UUIDUtils.name(uuid) + " has been made a member of " + team.getName() + ".");
                }
            });


        }

    }

}
