package net.frozenorb.foxtrot.settings.menu;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.settings.Setting;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class SettingButton extends Button {

    private final Setting setting;

    @Override
    public String getName(Player player) {
        return setting.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add("");
        description.addAll(setting.getDescription());
        description.add("");

        if (setting.isEnabled(player)) {
            description.add(ChatColor.BLUE.toString() + ChatColor.BOLD + "  ► " + setting.getEnabledText());
            description.add("    " + setting.getDisabledText());
        } else {
            description.add("    " + setting.getEnabledText());
            description.add(ChatColor.BLUE.toString() + ChatColor.BOLD + "  ► " + setting.getDisabledText());
        }

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return setting.getIcon();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        setting.toggle(player);
    }

}
