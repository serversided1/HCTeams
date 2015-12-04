package net.frozenorb.foxtrot.gui.button;

import lombok.AllArgsConstructor;
import net.frozenorb.Utilities.Interfaces.Callback;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BooleanButton extends Button {
    private boolean elAccepto;
    private Callback<Boolean> callback;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (elAccepto) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20f, 0.1F);
        }

        callback.callback(elAccepto);
    }

    @Override
    public String getName(Player player) {
        return elAccepto ? "§aConfirm" : "§cCancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return elAccepto ? (byte) 5 : (byte) 14;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }
}
